package org.example.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@ConfigurationProperties(prefix = "reward")
public record RewardAppProperties(Optional<Integer> periodMonths, Optional<List<Threshold>> thresholds) {
    @ConstructorBinding
    public RewardAppProperties(Integer periodMonths, List<Threshold> thresholds) {
        this(ofNullable(periodMonths), ofNullable(thresholds));
    }

}
