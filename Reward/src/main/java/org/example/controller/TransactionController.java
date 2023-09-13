package org.example.controller;

import org.example.entity.Transaction;
import org.example.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getAllTransactions(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getLastTransactions(userId));
    }

    @PostMapping("{userId}/{amount}")
    public ResponseEntity<Transaction> saveTransaction(@PathVariable Long userId, @PathVariable Double amount) {
        return ResponseEntity.ok(transactionService.addTransaction(userId, amount));
    }

    @PutMapping("/{userId}/{amount}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long userId, @PathVariable Double amount) {
        return ResponseEntity.ok(transactionService.updateLastTransaction(userId, amount));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<List<Transaction>> updateTransaction(@PathVariable Long userId) {
        transactionService.deleteLastTransaction(userId);
        return ResponseEntity.ok(transactionService.getAllTransactions(userId));
    }
}
