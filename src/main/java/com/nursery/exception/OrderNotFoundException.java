package com.nursery.exception;

/**
 * Thrown when an order lookup, update, or delete is attempted for an
 * order that does not exist.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
