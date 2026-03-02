package com.rakshan.codereview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning review data to the client.
 * Includes reviewer information, comments, status, and all dimension ratings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long codeSubmissionId;
    private String codeSubmissionTitle;
    private Long reviewerId;
    private String reviewerUsername;
    private String comments;
    private String status;
    private List<ReviewRatingDto> ratings;
    private Double averageScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
