package com.nursery.exception;

/**
 * Thrown when customer registration input fails validation -
 * e.g. a required field is missing/blank, or the email format is invalid.
 */
public class InvalidCustomerDataException extends RuntimeException {

    public InvalidCustomerDataException(String message) {
        super(message);
    }
}
