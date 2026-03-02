package com.rakshan.codereview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check controller for monitoring and deployment verification.
 * Provides a publicly accessible endpoint that the CI/CD pipeline
 * uses as a smoke test after deployment to confirm the application is running.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /** Returns application health status - used by CI/CD smoke tests */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", "Decentralised Peer Code Review Platform",
                "version", "1.0.0"
        ));
    }
}
