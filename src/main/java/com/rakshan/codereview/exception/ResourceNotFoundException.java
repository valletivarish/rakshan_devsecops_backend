package com.rakshan.codereview.exception;

/**
 * Custom exception thrown when a requested resource (entity) is not found in the database.
 * Returns HTTP 404 Not Found via the GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
