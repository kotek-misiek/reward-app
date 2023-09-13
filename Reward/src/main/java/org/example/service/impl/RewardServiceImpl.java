package org.example.service.impl;

import org.example.entity.Transaction;
import org.example.properties.RewardAppProperties;
import org.example.properties.Threshold;
import org.example.service.RewardService;
import org.example.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Service
public class RewardServiceImpl implements RewardService {
    private final List<Threshold> thresholds;
    private final TransactionService transactionService;

    public RewardServiceImpl(TransactionService transactionService, RewardAppProperties properties) {
        this.transactionService = transactionService;
        this.thresholds = properties.getThresholds();
    }

    public Double countReward(Long userId) {
        final var transactions = transactionService.getLastTransactions(userId);
        final var res = transactions
                .stream()
                .map(this::countTransactionReward)
                .reduce(ZERO, BigDecimal::add);
        return res.doubleValue();
    }

    private BigDecimal countTransactionReward(Transaction transaction) {
        final var amount = transaction.getAmount();
        var reward = ZERO;
        for (var i = 0; i < thresholds.size(); i++) {
            final var threshold = thresholds.get(i);
            final var currentLevel = threshold.getLevel();
            if (amount.compareTo(currentLevel) <= 0) {
                break;
            }
            final var points = BigDecimal.valueOf(threshold.getPoints());
            final var nextLevel = i + 1 >= thresholds.size()
                    ? BigDecimal.valueOf(Double.MAX_VALUE)
                    : thresholds.get(i + 1).getLevel();
            final var limit = amount.compareTo(nextLevel) > 0
                    ? nextLevel
                    : amount;
            reward = reward.add(limit.subtract(currentLevel).multiply(points));
        }
        return reward;
    }
}
