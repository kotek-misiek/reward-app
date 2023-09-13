package org.example.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "reward")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RewardAppProperties {
    private Integer periodMonths;
    private List<Threshold> thresholds;
}
