package org.example.service;

import org.example.output.CustomerRate;

import java.math.BigDecimal;
import java.util.List;

public interface RewardService {
    BigDecimal countReward(Long userId);
    CustomerRate countRewards(Long userId);
    List<CustomerRate> countRewards();
}
