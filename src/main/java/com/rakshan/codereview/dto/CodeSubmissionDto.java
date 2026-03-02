package com.rakshan.codereview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning code submission data to the client.
 * Includes submission details, status, author info, and assigned reviewer info.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmissionDto {
    private Long id;
    private String title;
    private String description;
    private String code;
    private String language;
    private String status;
    private Long userId;
    private String username;
    private Long assignedReviewerId;
    private String assignedReviewerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
