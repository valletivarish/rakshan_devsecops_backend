package com.rakshan.codereview.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility component for generating and validating JWT tokens.
 * Uses HMAC-SHA256 signing with a configurable secret key and expiration time.
 * Tokens contain the username as the subject claim and are used for stateless authentication.
 */
@Component
public class JwtTokenProvider {

    /** Secret key used to sign JWT tokens - loaded from application.properties */
    private final SecretKey key;

    /** Token expiration time in milliseconds - loaded from application.properties */
    private final long expiration;

    /**
     * Constructor injection of JWT configuration values.
     * Creates an HMAC-SHA key from the configured secret string.
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * Generates a JWT token for the given username.
     * Token contains the username as subject and expires after the configured duration.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the username (subject) from a valid JWT token.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates a JWT token by checking its signature and expiration.
     * Returns false if the token is malformed, expired, or has an invalid signature.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
