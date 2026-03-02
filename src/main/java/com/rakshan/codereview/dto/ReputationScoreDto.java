package com.rakshan.codereview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning reputation score data.
 * Used in the leaderboard and user profile views.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReputationScoreDto {
    private Long id;
    private Long userId;
    private String username;
    private Double totalScore;
    private Integer reviewCount;
    private Double averageAccuracy;
    private LocalDateTime updatedAt;
}
