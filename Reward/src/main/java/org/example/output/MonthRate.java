package org.example.output;

import org.example.enums.MonthEnum;

import java.math.BigDecimal;

public record MonthRate(MonthEnum month, BigDecimal points) {
}
