package com.nursery.exception;

/**
 * Thrown when a customer tries to register with a username that
 * already exists in the system.
 */
public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
