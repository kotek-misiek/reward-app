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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/all/{customerId}")
    public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable Long customerId) {
        return ResponseEntity.ok(transactionService.getAllTransactions(customerId));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Transaction>> getLastTransactions(@PathVariable Long customerId) {
        return ResponseEntity.ok(transactionService.getLastTransactions(customerId));
    }

    @PostMapping("{customerId}/{amount}")
    public ResponseEntity<Transaction> addTransaction(@PathVariable Long customerId, @PathVariable BigDecimal amount) {
        return ResponseEntity.ok(transactionService.addTransaction(customerId, amount));
    }

    @PutMapping("/{customerId}/{amount}")
    public ResponseEntity<Transaction> updateLastTransaction(@PathVariable Long customerId, @PathVariable BigDecimal amount) {
        return ResponseEntity.ok(transactionService.updateLastTransaction(customerId, amount));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<List<Transaction>> deleteLastTransaction(@PathVariable Long customerId) {
        transactionService.deleteLastTransaction(customerId);
        return ResponseEntity.ok(transactionService.getLastTransactions(customerId));
    }
}
