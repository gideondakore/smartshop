package com.amalitech.smartshop.exceptions;

/**
 * Exception thrown when attempting to create a resource that already exists.
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(resourceName + " already exists with identifier: " + identifier);
    }
}
