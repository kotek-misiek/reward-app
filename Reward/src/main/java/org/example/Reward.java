package org.example;

import org.example.properties.RewardAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RewardAppProperties.class)
public class Reward {
    public static void main(String[] args) {
        SpringApplication.run(Reward.class, args);
    }
}