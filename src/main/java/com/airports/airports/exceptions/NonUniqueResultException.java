package com.airports.airports.exceptions;

public class NonUniqueResultException extends RuntimeException {
    public NonUniqueResultException(String message) {
        super(message);
    }

    public NonUniqueResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
