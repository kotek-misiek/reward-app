package org.example.enums;

import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
public enum MonthEnum {
    JANUARY(1, "January"), FEBRUARY(2, "February"), MARCH(3, "March"), APRIL(4, "April"),
    MAY(5, "May"), JUNE(6, "June"), JULY(7, "July"), AUGUST(8, "August"),
    SEPTEMBER(9, "September"), OCTOBER(10, "October"), NOVEMBER(11, "November"), DECEMBER(12, "December"),
    TOTAL(13, "Total");

    private final int number;
    private final String value;

    MonthEnum(int number, String value) {
        this.number = number;
        this.value = value;
    }

    public static MonthEnum byNumber(int number) {
        if (number <= 0 || number > 12) {
            return TOTAL;
        }
        return stream(values())
                .filter(item -> item.number == number)
                .findFirst()
                .orElse(TOTAL);
    }
}
