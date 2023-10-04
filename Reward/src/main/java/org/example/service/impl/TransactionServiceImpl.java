package org.example.service.impl;

import org.example.entity.Account;
import org.example.entity.Transaction;
import org.example.enums.MonthEnum;
import org.example.enums.TransactionTypeEnum;
import org.example.exceptions.TransactionNotFoundException;
import org.example.exceptions.UnacceptableAmountException;
import org.example.properties.RewardAppProperties;
import org.example.repository.AccountRepository;
import org.example.repository.CustomerRepository;
import org.example.repository.TransactionRepository;
import org.example.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static org.example.enums.TransactionTypeEnum.U;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final static Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private static final String NO_USER = "User with ID = %s not found";
    private static final String NO_ACCOUNT = "Account belonging to user %s %s not found";
    private static final Integer BASIC_PERIOD_MONTHS = 3;
    private final Integer periodMonths;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(CustomerRepository customerRepository, AccountRepository accountRepository,
                                  TransactionRepository transactionRepository, RewardAppProperties properties) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.periodMonths = properties.periodMonths().orElse(BASIC_PERIOD_MONTHS);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        LOG.info("START getAllTransactions()");
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getAllTransactions(Long userId) {
        LOG.info("START getAllTransactions({})", userId);
        final var account = findAccount(userId);
        return transactionRepository.findByAccountId(account.getId());
    }

    @Override
    public List<Transaction> getLastTransactions(Long userId) {
        LOG.info("START getLastTransactions({})", userId);
        final var account = findAccount(userId);
        final var updatedTimeEnd = Timestamp.valueOf(LocalDateTime.now().minusMonths(periodMonths));
        return transactionRepository.findByAccountIdAndUpdateTimeGreaterThan(account.getId(), updatedTimeEnd);
    }

    @Override
    public Map<MonthEnum, List<Transaction>> getLastTransactionsByMonths(Long userId) {
        return getLastTransactions(userId)
                .stream()
                .collect(groupingBy(transaction -> {
                    final var cal = Calendar.getInstance();
                    cal.setTimeInMillis(transaction.getUpdateTime().getTime());
                    return MonthEnum.byNumber(cal.get(Calendar.MONTH));
                }));
    }

    @Override
    public Transaction addTransaction(Long userId, BigDecimal amount) {
        LOG.info("START addTransaction({}, {})", userId, amount);
        if (amount.compareTo(ZERO) <= 0) {
            throw new UnacceptableAmountException(amount);
        }
        final var account = findAccount(userId);
        account.setAmount(account.getAmount().add(amount));
        return transactionRepository.save(Transaction.builder()
                .account(account)
                .amount(amount)
                .transactionType(TransactionTypeEnum.A)
                .updateTime(Timestamp.valueOf(LocalDateTime.now()))
                .build());
    }

    @Override
    @Transactional(isolation = SERIALIZABLE)
    public Transaction updateLastTransaction(Long userId, BigDecimal amount) {
        LOG.info("START updateLastTransaction({}, {})", userId, amount);
        if (amount.compareTo(ZERO) <= 0) {
            throw new UnacceptableAmountException(amount);
        }
        final var account = findAccount(userId);
        final var id = transactionRepository.findLastId(account.getId())
                .orElseThrow(() -> new TransactionNotFoundException(0L));
        final var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        account.setAmount(account.getAmount().subtract(transaction.getAmount()).add(amount));

        transaction.setTransactionType(U);
        transaction.setAmount(amount);
        transaction.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(isolation = SERIALIZABLE)
    public void deleteLastTransaction(Long userId) {
        LOG.info("START deleteLastTransaction({})", userId);
        final var account = findAccount(userId);
        final var id = transactionRepository.findLastId(account.getId())
                .orElseThrow(() -> new TransactionNotFoundException(0L));
        final var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        account.setAmount(account.getAmount().subtract(transaction.getAmount()));
        transactionRepository.deleteById(id);
    }

    private Account findAccount(Long userId) {
        final var user = customerRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(format(NO_USER, userId)));
        return accountRepository.findByCustomerId(userId)
                .orElseThrow(
                        () -> new NoSuchElementException(format(NO_ACCOUNT, user.getFirstName(), user.getLastName())));
    }
}
