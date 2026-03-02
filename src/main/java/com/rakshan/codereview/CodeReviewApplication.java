package com.rakshan.codereview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Decentralised Peer Code Review Platform.
 * This Spring Boot application provides a REST API for managing code submissions,
 * peer reviews, reviewer reputation scores, and review quality predictions.
 */
@SpringBootApplication
public class CodeReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeReviewApplication.class, args);
    }
}
