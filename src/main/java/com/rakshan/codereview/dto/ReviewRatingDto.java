package com.rakshan.codereview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for individual dimension ratings within a review.
 * Each rating links a dimension ID to a numeric score (1-5).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRatingDto {

    /** ID of the review dimension being rated */
    @NotNull(message = "Dimension ID is required")
    private Long dimensionId;

    /** Name of the dimension (populated in response, not required in request) */
    private String dimensionName;

    /** Score assigned for this dimension - must be between 1 and the dimension's max score (up to 10) */
    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 10, message = "Score must not exceed 10")
    private Integer score;
}
