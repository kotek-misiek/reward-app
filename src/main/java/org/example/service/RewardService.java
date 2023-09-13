package org.example.service;

import org.example.entity.Transaction;

import java.util.List;

public interface RewardService {
    List<Transaction> getTransactions();
    List<Transaction> getTransactions(Long userId);
    Transaction addTransaction(Long userId, Double amount);
    Transaction updateLastTransaction(Long userId, Double amount);
    void deleteLastTransaction(Long userId);
}
