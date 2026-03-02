package com.rakshan.codereview.service;

import com.rakshan.codereview.config.JwtTokenProvider;
import com.rakshan.codereview.dto.auth.AuthResponse;
import com.rakshan.codereview.dto.auth.LoginRequest;
import com.rakshan.codereview.dto.auth.RegisterRequest;
import com.rakshan.codereview.exception.BadRequestException;
import com.rakshan.codereview.exception.UnauthorizedException;
import com.rakshan.codereview.model.ReputationScore;
import com.rakshan.codereview.model.User;
import com.rakshan.codereview.model.enums.Role;
import com.rakshan.codereview.repository.ReputationScoreRepository;
import com.rakshan.codereview.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user authentication operations: registration and login.
 * On registration, creates a new user with hashed password and initialises their reputation score.
 * On login, validates credentials and returns a JWT token for subsequent authenticated requests.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ReputationScoreRepository reputationScoreRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /** Constructor injection of all dependencies */
    public AuthService(UserRepository userRepository,
                       ReputationScoreRepository reputationScoreRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.reputationScoreRepository = reputationScoreRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user account.
     * Validates uniqueness of username and email, hashes the password with BCrypt,
     * creates the user record, and initialises a reputation score entry.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate username and email are not already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        // Create new user with hashed password and default USER role
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        user = userRepository.save(user);

        // Initialise reputation score for the new user with zero values
        ReputationScore reputationScore = ReputationScore.builder()
                .user(user)
                .totalScore(0.0)
                .reviewCount(0)
                .averageAccuracy(0.0)
                .build();
        reputationScoreRepository.save(reputationScore);

        // Generate JWT token and return authentication response
        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }

    /**
     * Authenticates an existing user with username and password.
     * Validates credentials and returns a JWT token on success.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Find user by username or throw unauthorised exception
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        // Verify password matches the stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // Generate JWT token and return authentication response
        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
