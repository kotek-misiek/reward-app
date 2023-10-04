package org.example.output;

import java.util.List;

public record CustomerRate(String name, List<MonthRate> monthRate) {
}
