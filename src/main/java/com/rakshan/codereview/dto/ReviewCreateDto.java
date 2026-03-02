package com.rakshan.codereview.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating or updating a review.
 * Contains the code submission ID, reviewer comments, and individual dimension ratings.
 * Comment length is limited to 5000 characters to prevent abuse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDto {

    /** ID of the code submission being reviewed */
    @NotNull(message = "Code submission ID is required")
    private Long codeSubmissionId;

    /** Reviewer's comments and feedback - max 5000 characters */
    @Size(max = 5000, message = "Comments must not exceed 5000 characters")
    private String comments;

    /** List of dimension ratings - each rating is validated individually */
    @Valid
    private List<ReviewRatingDto> ratings;
}
