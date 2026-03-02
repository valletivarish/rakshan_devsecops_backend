package com.rakshan.codereview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for structured error responses returned by the GlobalExceptionHandler.
 * Provides consistent error format across all API endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** HTTP status code */
    private int status;

    /** Error message describing what went wrong */
    private String message;

    /** Timestamp when the error occurred */
    private LocalDateTime timestamp;

    /** Map of field-specific validation errors (field name -> error message) */
    private Map<String, String> errors;

    /**
     * Constructor for simple error responses without field-level errors.
     */
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
