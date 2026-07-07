package com.nursery.exception;

/**
 * Thrown when a seed lookup, update, or delete is attempted for a
 * seed that does not exist.
 */
public class SeedNotFoundException extends RuntimeException {

    public SeedNotFoundException(String message) {
        super(message);
    }
}
