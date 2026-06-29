package com.nursery.exception;

/**
 * Thrown when a plant lookup, update, or delete is attempted for a
 * plant that does not exist.
 */
public class PlantNotFoundException extends RuntimeException {

    public PlantNotFoundException(String message) {
        super(message);
    }
}
