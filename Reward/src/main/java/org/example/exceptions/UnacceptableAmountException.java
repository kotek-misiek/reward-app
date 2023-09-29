package org.example.exceptions;

import java.math.BigDecimal;

public class UnacceptableAmountException extends RuntimeException {
    public UnacceptableAmountException(BigDecimal amount) {
        super(String.format("Unacceptable amount = %s", amount));
    }
}
