package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.auth.AuthResponse;
import com.rakshan.codereview.dto.auth.LoginRequest;
import com.rakshan.codereview.dto.auth.RegisterRequest;
import com.rakshan.codereview.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Provides public endpoints for user registration and login.
 * Returns JWT tokens for authenticated sessions.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Register a new user account and receive a JWT token */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with username, email, and password")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Authenticate with username and password to receive a JWT token */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with username and password to receive a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
