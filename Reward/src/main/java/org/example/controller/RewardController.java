package org.example.controller;

import org.example.output.CustomerRate;
import org.example.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/reward")
public class RewardController {
    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<BigDecimal> getReward(@PathVariable Long customerId) {
        return ResponseEntity.ok(rewardService.countReward(customerId));
    }

    @GetMapping("/table/{customerId}")
    public ResponseEntity<CustomerRate> getRewardTable(@PathVariable Long customerId) {
        return ResponseEntity.ok(rewardService.countRewards(customerId));
    }

    @GetMapping("/table")
    public ResponseEntity<List<CustomerRate>> getRewardTable() {
        return ResponseEntity.ok(rewardService.countRewards());
    }
}
