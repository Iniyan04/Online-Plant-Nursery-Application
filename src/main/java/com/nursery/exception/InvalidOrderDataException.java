package com.nursery.exception;

/**
 * Thrown when order data fails validation - e.g. quantity is invalid,
 * stock is insufficient, or required fields are missing.
 */
public class InvalidOrderDataException extends RuntimeException {

    public InvalidOrderDataException(String message) {
        super(message);
    }
}
