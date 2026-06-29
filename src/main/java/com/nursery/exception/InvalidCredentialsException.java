package com.nursery.exception;

/**
 * Thrown when a login attempt fails because the username does not exist
 * or the provided password does not match.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
