package com.nursery.exception;

/**
 * Thrown when plant data fails validation - e.g. a required field is
 * blank, or cost/stock is negative.
 */
public class InvalidPlantDataException extends RuntimeException {

    public InvalidPlantDataException(String message) {
        super(message);
    }
}
