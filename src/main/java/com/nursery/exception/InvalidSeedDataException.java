package com.nursery.exception;

/**
 * Thrown when seed data fails validation - e.g. a required field is
 * blank, or cost/stock is negative.
 */
public class InvalidSeedDataException extends RuntimeException {

    public InvalidSeedDataException(String message) {
        super(message);
    }
}
