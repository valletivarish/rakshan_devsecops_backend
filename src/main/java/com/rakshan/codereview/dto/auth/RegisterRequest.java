package com.rakshan.codereview.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests.
 * Validates username length (3-50 chars), email format, and password strength (min 6 chars).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** Username must be between 3 and 50 characters */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /** Email must be a valid email format */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    /** Password must be at least 6 characters long for security */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
