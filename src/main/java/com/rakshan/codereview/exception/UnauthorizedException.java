package com.rakshan.codereview.exception;

/**
 * Custom exception thrown when a user attempts an action they are not authorised to perform.
 * Returns HTTP 401 Unauthorized via the GlobalExceptionHandler.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
