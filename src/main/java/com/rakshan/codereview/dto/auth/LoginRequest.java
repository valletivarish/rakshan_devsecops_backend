package com.rakshan.codereview.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 * Validates that both username and password are provided and non-blank.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Username used for authentication - must not be blank */
    @NotBlank(message = "Username is required")
    private String username;

    /** Password used for authentication - must not be blank */
    @NotBlank(message = "Password is required")
    private String password;
}
