package org.example.service.impl;

import org.example.entity.Transaction;
import org.example.enums.TransactionTypeEnum;
import org.example.exceptions.TransactionNotFoundException;
import org.example.exceptions.UnacceptableAmountException;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.service.RewardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static org.example.enums.TransactionTypeEnum.U;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
public class RewardServiceImpl implements RewardService {
    private static final String NO_USER = "User with ID = %s not found";
    private static final String NO_ACCOUNT = "Account belonging to user %s %s not found";
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public RewardServiceImpl(UserRepository userRepository, AccountRepository accountRepository,
                             TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getTransactions(Long userId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(format(NO_USER, userId)));
        final var account = accountRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new NoSuchElementException(format(NO_ACCOUNT, user.getFirstName(), user.getLastName())));
        return transactionRepository.findByAccountId(account.getId());
    }

    @Override
    public Transaction addTransaction(Long userId, Double amount) {
        if (amount <= 0) {
            throw new UnacceptableAmountException(amount);
        }
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(format(NO_USER, userId)));
        final var account = accountRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new NoSuchElementException(format(NO_ACCOUNT, user.getFirstName(), user.getLastName())));
        final var bdAmount = BigDecimal.valueOf(amount);
        account.setAmount(account.getAmount().add(bdAmount));
        return transactionRepository.save(Transaction.builder()
                .account(account)
                .amount(bdAmount)
                .transactionType(TransactionTypeEnum.A)
                .deleted(false)
                .build());
    }

    @Override
    @Transactional(isolation = SERIALIZABLE)
    public Transaction updateLastTransaction(Long userId, Double amount) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(format(NO_USER, userId)));
        final var account = accountRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new NoSuchElementException(format(NO_ACCOUNT, user.getFirstName(), user.getLastName())));
        final var bdAmount = BigDecimal.valueOf(amount);
        final var id = transactionRepository.findLastId(account.getId())
                .orElseThrow(() -> new TransactionNotFoundException(0L));
        final var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        account.setAmount(account.getAmount().subtract(transaction.getAmount()).add(bdAmount));

        transaction.setTransactionType(U);
        transaction.setAmount(bdAmount);
        transaction.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(isolation = SERIALIZABLE)
    public void deleteLastTransaction(Long userId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(format(NO_USER, userId)));
        final var account = accountRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new NoSuchElementException(format(NO_ACCOUNT, user.getFirstName(), user.getLastName())));
        final var id = transactionRepository.findLastId(account.getId())
                .orElseThrow(() -> new TransactionNotFoundException(0L));
        final var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        account.setAmount(account.getAmount().subtract(transaction.getAmount()));
        transactionRepository.deleteById(id);
    }
}
