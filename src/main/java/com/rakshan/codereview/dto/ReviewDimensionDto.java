package com.rakshan.codereview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning review dimension data to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDimensionDto {
    private Long id;
    private String name;
    private String description;
    private Integer maxScore;
    private LocalDateTime createdAt;
}
