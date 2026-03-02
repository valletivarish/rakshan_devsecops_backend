package com.rakshan.codereview.exception;

/**
 * Custom exception thrown when a client request contains invalid data or violates business rules.
 * Returns HTTP 400 Bad Request via the GlobalExceptionHandler.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
