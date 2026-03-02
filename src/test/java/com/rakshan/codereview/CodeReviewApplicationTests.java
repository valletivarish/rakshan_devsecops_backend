package com.rakshan.codereview;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test verifying that the Spring application context loads successfully.
 * Uses the 'test' profile with H2 in-memory database.
 */
@SpringBootTest
@ActiveProfiles("test")
class CodeReviewApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context initialises without errors
    }
}
