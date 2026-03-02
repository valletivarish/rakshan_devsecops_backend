package com.rakshan.codereview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for the dashboard summary data.
 * Provides aggregate statistics for the platform overview.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    /** Total number of code submissions on the platform */
    private Long totalSubmissions;

    /** Total number of completed reviews */
    private Long totalReviews;

    /** Total number of registered users */
    private Long totalUsers;

    /** Number of submissions currently pending review */
    private Long pendingReviews;

    /** Number of submissions currently under review */
    private Long underReview;

    /** Number of submissions that have been reviewed */
    private Long completedReviews;

    /** Count of submissions grouped by programming language */
    private Map<String, Long> submissionsByLanguage;

    /** Average score across all completed reviews */
    private Double averageReviewScore;
}
