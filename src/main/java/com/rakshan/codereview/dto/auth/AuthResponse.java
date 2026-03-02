package com.rakshan.codereview.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned after successful authentication (login or registration).
 * Contains the JWT token and basic user information for the frontend to store in context.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** JWT access token for authenticating subsequent API requests */
    private String token;

    /** Authenticated user's unique identifier */
    private Long userId;

    /** Authenticated user's username */
    private String username;

    /** Authenticated user's role (USER or ADMIN) */
    private String role;
}
