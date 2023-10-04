package org.example.service.impl;

import org.example.entity.Transaction;
import org.example.output.CustomerRate;
import org.example.output.MonthRate;
import org.example.properties.RewardAppProperties;
import org.example.properties.Threshold;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.service.RewardService;
import org.example.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static org.example.enums.MonthEnum.TOTAL;

@Service
public class RewardServiceImpl implements RewardService {
    private static final Logger LOG = LoggerFactory.getLogger(RewardServiceImpl.class);
    private static final List<Threshold> BASIC_TRESHOLDS = List.of(
            new Threshold(BigDecimal.valueOf(50L), 1),
            new Threshold(BigDecimal.valueOf(100L), 2));
    private final List<Threshold> thresholds;
    private final AccountService accountService;
    private final CustomerService customerService;
    private final TransactionService transactionService;

    public RewardServiceImpl(RewardAppProperties properties, AccountService accountService, CustomerService customerService, TransactionService transactionService) {
        this.thresholds = properties.thresholds().orElse(BASIC_TRESHOLDS);
        this.accountService = accountService;
        this.customerService = customerService;
        this.transactionService = transactionService;
    }

    public BigDecimal countReward(Long customerId) {
        LOG.info("countReward start customerId = {}", customerId);
        final var transactions = transactionService.getLastTransactions(customerId);
        return transactions
                .stream()
                .map(this::countTransactionReward)
                .reduce(ZERO, BigDecimal::add);
    }

    @Override
    public CustomerRate countRewards(Long customerId) {
        LOG.info("countRewards start customerId = {}", customerId);
        final var transactions = transactionService.getLastTransactionsByMonths(customerId);
        final var customer = customerService.getById(customerId);
        final var list = transactions
                .entrySet()
                .stream()
                .map(entry -> new MonthRate(entry.getKey(),
                        entry.getValue()
                                .stream()
                                .map(this::countTransactionReward)
                                .reduce(ZERO, BigDecimal::add)))
                .sorted(comparingInt(rate -> rate.month().getNumber()))
                .collect(toList());
        list.add(new MonthRate(TOTAL,
                list
                        .stream()
                        .map(MonthRate::points)
                        .reduce(ZERO, BigDecimal::add)));
        return new CustomerRate(format("%s %s", customer.getFirstName(), customer.getLastName()), list);
    }

    @Override
    public List<CustomerRate> countRewards() {
        LOG.info("countRewards start");
        return customerService.findAll()
                .stream()
                .filter(customer -> !accountService.isEmpty(customer.getId()))
                .map(customer -> countRewards(customer.getId()))
                .collect(toList());
    }

    private BigDecimal countTransactionReward(Transaction transaction) {
        final var amount = transaction.getAmount();
        var reward = ZERO;
        for (var i = 0; i < thresholds.size(); i++) {
            final var threshold = thresholds.get(i);
            final var currentLevel = threshold.level();
            if (amount.compareTo(currentLevel) <= 0) {
                break;
            }
            final var points = BigDecimal.valueOf(threshold.points());
            final var nextLevel = i + 1 >= thresholds.size()
                    ? BigDecimal.valueOf(Double.MAX_VALUE)
                    : thresholds.get(i + 1).level();
            final var limit = amount.compareTo(nextLevel) > 0
                    ? nextLevel
                    : amount;
            reward = reward.add(limit.subtract(currentLevel).multiply(points));
        }
        return reward;
    }
}
