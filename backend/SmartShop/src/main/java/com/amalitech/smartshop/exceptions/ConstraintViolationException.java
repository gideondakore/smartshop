package com.amalitech.smartshop.exceptions;

/**
 * Exception thrown when a database constraint violation occurs.
 */
public class ConstraintViolationException extends RuntimeException {
    
    public ConstraintViolationException(String message) {
        super(message);
    }
}
