package com.nursery.exception;

/**
 * Thrown when a planter lookup, update, or delete is attempted for a
 * planter that does not exist.
 */
public class PlanterNotFoundException extends RuntimeException {

    public PlanterNotFoundException(String message) {
        super(message);
    }
}
