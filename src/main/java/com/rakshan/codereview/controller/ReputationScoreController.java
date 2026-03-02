package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.ReputationScoreDto;
import com.rakshan.codereview.service.ReputationScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for reputation scores and the reviewer leaderboard.
 * The leaderboard endpoint is public (no authentication required) to allow
 * visitors to see top reviewers. Individual reputation lookup requires authentication.
 */
@RestController
@RequestMapping("/api/reputation")
@Tag(name = "Reputation Scores", description = "Reputation score and leaderboard endpoints")
public class ReputationScoreController {

    private final ReputationScoreService reputationScoreService;

    public ReputationScoreController(ReputationScoreService reputationScoreService) {
        this.reputationScoreService = reputationScoreService;
    }

    /** Get the reviewer leaderboard - sorted by total reputation score (public endpoint) */
    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard", description = "Retrieve all reviewers ranked by reputation score")
    public ResponseEntity<List<ReputationScoreDto>> getLeaderboard() {
        return ResponseEntity.ok(reputationScoreService.getLeaderboard());
    }

    /** Get reputation score for a specific user */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user reputation", description = "Retrieve reputation score for a specific user")
    public ResponseEntity<ReputationScoreDto> getReputationByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reputationScoreService.getReputationByUserId(userId));
    }
}
