package org.example.controller;

import org.example.entity.Transaction;
import org.example.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RewardController {
    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions() {
        return ResponseEntity.ok(rewardService.getTransactions());
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getTransactions(userId));
    }

    @PostMapping("/transactions/{userId}/{amount}")
    public ResponseEntity<Transaction> saveTransaction(@PathVariable Long userId, @PathVariable Double amount) {
        return ResponseEntity.ok(rewardService.addTransaction(userId, amount));
    }

    @PutMapping("/transactions/{userId}/{amount}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long userId, @PathVariable Double amount) {
        return ResponseEntity.ok(rewardService.updateLastTransaction(userId, amount));
    }

    @DeleteMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> updateTransaction(@PathVariable Long userId) {
        rewardService.deleteLastTransaction(userId);
        return ResponseEntity.ok(rewardService.getTransactions(userId));
    }
}
