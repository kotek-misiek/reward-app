package org.example.service.impl;

import org.example.properties.RewardAppProperties;
import org.example.service.RewardService;
import org.springframework.stereotype.Service;

@Service
public class RewardServiceImpl implements RewardService {
    private final RewardAppProperties properties;

    public RewardServiceImpl(RewardAppProperties properties) {
        this.properties = properties;
    }

    public Double countReward(Long userId) {

        return 0.0;
    }
}
