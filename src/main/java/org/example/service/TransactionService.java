package org.example.service;

import org.example.entity.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    List<Transaction> getAllTransactions(Long userId);
    List<Transaction> getLastTransactions(Long userId);
    Transaction addTransaction(Long userId, Double amount);
    Transaction updateLastTransaction(Long userId, Double amount);
    void deleteLastTransaction(Long userId);
}
