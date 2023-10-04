package org.example.service;

import org.example.entity.Transaction;
import org.example.enums.MonthEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    List<Transaction> getAllTransactions(Long userId);
    List<Transaction> getLastTransactions(Long userId);
    Map<MonthEnum, List<Transaction>> getLastTransactionsByMonths(Long userId);
    Transaction addTransaction(Long userId, BigDecimal amount);
    Transaction updateLastTransaction(Long userId, BigDecimal amount);
    void deleteLastTransaction(Long userId);
}
