package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.UserDto;
import com.rakshan.codereview.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user profile management.
 * Provides endpoints to list, view, and delete user accounts.
 * All endpoints require authentication (JWT token).
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** Get all registered users */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /** Get a specific user by ID */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user profile by their ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /** Delete a user account by ID */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user account by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
