package com.rakshan.codereview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 configuration for the application.
 * Configures stateless JWT-based authentication, CORS, CSRF settings,
 * and endpoint-level access control rules.
 * Public endpoints: authentication, Swagger docs, health check.
 * All other endpoints require a valid JWT token.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /** Constructor injection of the JWT filter */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configures the security filter chain with JWT authentication.
     * Disables CSRF (since we use stateless JWT tokens, not session cookies).
     * Defines public and protected endpoint patterns.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since JWT tokens are not vulnerable to CSRF attacks
                .csrf(csrf -> csrf.disable())
                // Use stateless session management - no server-side session storage
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define endpoint access rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        // Swagger/OpenAPI documentation endpoints
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        // Allow GET requests to dashboard and leaderboard without authentication
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reputation/leaderboard").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated())
                // Add JWT filter before the default username/password authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** BCrypt password encoder for hashing user passwords */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Authentication manager bean for processing login requests */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
