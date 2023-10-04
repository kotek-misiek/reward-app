package org.example.enums;

import lombok.Getter;

@Getter
public enum TransactionTypeEnum {
    A("Added"), U("Updated");

    private final String value;

    TransactionTypeEnum(String value) {
        this.value = value;
    }
}
