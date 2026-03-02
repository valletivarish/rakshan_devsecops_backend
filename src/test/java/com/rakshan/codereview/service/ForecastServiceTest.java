package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.ForecastDto;
import com.rakshan.codereview.model.*;
import com.rakshan.codereview.model.enums.*;
import com.rakshan.codereview.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ForecastService.
 * Tests the ML prediction feature using Apache Commons Math SimpleRegression.
 * Verifies prediction accuracy with mock historical review data.
 */
@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ForecastService forecastService;

    private List<Review> testReviews;

    @BeforeEach
    void setUp() {
        // Build test reviews with varying code lengths and scores
        User author = User.builder().id(1L).username("author").email("a@test.com")
                .password("pass").role(Role.USER).build();
        User reviewerUser = User.builder().id(2L).username("reviewer").email("r@test.com")
                .password("pass").role(Role.USER).build();

        ReviewDimension readability = ReviewDimension.builder()
                .id(1L).name("Readability").maxScore(5).build();

        testReviews = new ArrayList<>();
        // Create 5 reviews with increasing code lengths and scores
        int[] codeLengths = {100, 200, 300, 400, 500};
        int[] scores = {2, 3, 3, 4, 4};

        for (int i = 0; i < 5; i++) {
            CodeSubmission sub = CodeSubmission.builder().id((long) i + 1)
                    .title("Sub " + i).code("x".repeat(codeLengths[i]))
                    .language(Language.JAVA).status(SubmissionStatus.REVIEWED)
                    .user(author).build();

            ReviewRating rating = ReviewRating.builder().id((long) i + 1)
                    .dimension(readability).score(scores[i]).build();

            Review review = Review.builder().id((long) i + 1)
                    .codeSubmission(sub).reviewer(reviewerUser)
                    .status(ReviewStatus.SUBMITTED)
                    .ratings(new ArrayList<>(List.of(rating)))
                    .createdAt(LocalDateTime.now())
                    .build();

            rating.setReview(review);
            testReviews.add(review);
        }
    }

    /** Test prediction returns valid result with sufficient data */
    @Test
    void predictQualityScore_WithSufficientData_ReturnsPrediction() {
        when(reviewRepository.findByStatus(ReviewStatus.SUBMITTED)).thenReturn(testReviews);

        ForecastDto result = forecastService.predictQualityScore(350);

        assertNotNull(result);
        assertTrue(result.getPredictedScore() >= 0.0 && result.getPredictedScore() <= 5.0);
        assertNotNull(result.getTrendDirection());
        assertTrue(result.getDataPointsUsed() >= 3);
    }

    /** Test prediction with insufficient data returns appropriate message */
    @Test
    void predictQualityScore_InsufficientData_ReturnsInsufficientMessage() {
        when(reviewRepository.findByStatus(ReviewStatus.SUBMITTED))
                .thenReturn(Collections.singletonList(testReviews.get(0)));

        ForecastDto result = forecastService.predictQualityScore(350);

        assertEquals("INSUFFICIENT_DATA", result.getTrendDirection());
        assertEquals(0.0, result.getConfidence());
    }

    /** Test trend prediction returns valid result */
    @Test
    void predictScoreTrend_WithData_ReturnsTrend() {
        when(reviewRepository.findByStatus(ReviewStatus.SUBMITTED)).thenReturn(testReviews);

        ForecastDto result = forecastService.predictScoreTrend(5);

        assertNotNull(result);
        assertTrue(result.getPredictedScore() >= 0.0 && result.getPredictedScore() <= 5.0);
        assertTrue(Arrays.asList("IMPROVING", "DECLINING", "STABLE")
                .contains(result.getTrendDirection()));
    }

    /** Test confidence score is between 0 and 1 */
    @Test
    void predictQualityScore_ConfidenceInRange() {
        when(reviewRepository.findByStatus(ReviewStatus.SUBMITTED)).thenReturn(testReviews);

        ForecastDto result = forecastService.predictQualityScore(300);

        assertTrue(result.getConfidence() >= 0.0 && result.getConfidence() <= 1.0);
    }
}
