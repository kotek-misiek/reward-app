package org.example.exceptions;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super(id.equals(0L)
                ? "No such of transaction"
                : String.format("Transaction #%s not found", id));
    }
}
