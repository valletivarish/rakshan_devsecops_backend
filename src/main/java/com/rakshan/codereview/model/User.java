package com.rakshan.codereview.model;

import com.rakshan.codereview.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a registered user in the code review platform.
 * Users can submit code for review, be assigned as reviewers, and accumulate reputation scores.
 * Each user has a unique username and email, and is assigned a role (USER or ADMIN).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique username chosen during registration */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Unique email address used for login and notifications */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** BCrypt-hashed password for authentication */
    @Column(nullable = false)
    private String password;

    /** Role determining access level: USER or ADMIN */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Timestamp when the user account was created */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the user account was last updated */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
