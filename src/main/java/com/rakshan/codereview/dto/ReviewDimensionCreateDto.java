package com.rakshan.codereview.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a review dimension (criterion).
 * Validates name length (max 100), description length (max 500),
 * and max score range (1-5 to keep scoring consistent).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDimensionCreateDto {

    /** Name of the dimension - unique, max 100 characters */
    @NotBlank(message = "Dimension name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    /** Description of what this dimension evaluates - max 500 characters */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /** Maximum score value for this dimension - must be between 1 and 5 */
    @NotNull(message = "Max score is required")
    @Min(value = 1, message = "Max score must be at least 1")
    @Max(value = 5, message = "Max score must not exceed 5")
    private Integer maxScore;
}
