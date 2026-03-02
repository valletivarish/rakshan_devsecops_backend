package com.rakshan.codereview.repository;

import com.rakshan.codereview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity providing CRUD operations and custom queries.
 * Includes methods for finding users by username or email for authentication,
 * and checking existence to prevent duplicate registrations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find a user by their username - used during login authentication */
    Optional<User> findByUsername(String username);

    /** Find a user by their email address */
    Optional<User> findByEmail(String email);

    /** Check if a username is already taken - used during registration validation */
    boolean existsByUsername(String username);

    /** Check if an email is already registered - used during registration validation */
    boolean existsByEmail(String email);
}
