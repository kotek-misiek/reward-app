package org.example.exceptions;

public class UnacceptableAmountException extends RuntimeException {
    public UnacceptableAmountException(Double amount) {
        super(String.format("Unacceptable amount = %s", amount));
    }
}
