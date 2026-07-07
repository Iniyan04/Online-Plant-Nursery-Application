package com.nursery.exception;

/**
 * Thrown when planter data fails validation - e.g. a required field is
 * blank, or stock/cost is negative.
 */
public class InvalidPlanterDataException extends RuntimeException {

    public InvalidPlanterDataException(String message) {
        super(message);
    }
}
