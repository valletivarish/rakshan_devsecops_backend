package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.ReviewCreateDto;
import com.rakshan.codereview.dto.ReviewDto;
import com.rakshan.codereview.dto.ReviewRatingDto;
import com.rakshan.codereview.exception.BadRequestException;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import com.rakshan.codereview.model.*;
import com.rakshan.codereview.model.enums.*;
import com.rakshan.codereview.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReviewService.
 * Tests review creation, validation rules, and business logic using Mockito mocks.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CodeSubmissionRepository codeSubmissionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewDimensionRepository reviewDimensionRepository;
    @Mock
    private ReputationScoreRepository reputationScoreRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User author;
    private User reviewer;
    private CodeSubmission submission;
    private ReviewDimension dimension;
    private Review testReview;

    @BeforeEach
    void setUp() {
        author = User.builder().id(1L).username("author").email("author@test.com")
                .password("pass").role(Role.USER).createdAt(LocalDateTime.now()).build();

        reviewer = User.builder().id(2L).username("reviewer").email("reviewer@test.com")
                .password("pass").role(Role.USER).createdAt(LocalDateTime.now()).build();

        submission = CodeSubmission.builder().id(1L).title("Test Code").description("desc")
                .code("System.out.println('hello');").language(Language.JAVA)
                .status(SubmissionStatus.UNDER_REVIEW).user(author).assignedReviewer(reviewer)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        dimension = ReviewDimension.builder().id(1L).name("Readability")
                .description("Code readability").maxScore(5).createdAt(LocalDateTime.now()).build();

        testReview = Review.builder().id(1L).codeSubmission(submission).reviewer(reviewer)
                .comments("Good code").status(ReviewStatus.SUBMITTED)
                .ratings(new ArrayList<>()).createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
    }

    /** Test creating a review with valid data */
    @Test
    void createReview_ValidInput_ReturnsDto() {
        ReviewRatingDto ratingDto = new ReviewRatingDto(1L, null, 4);
        ReviewCreateDto createDto = new ReviewCreateDto(1L, "Good work", List.of(ratingDto));

        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reviewer));
        when(reviewDimensionRepository.findById(1L)).thenReturn(Optional.of(dimension));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(codeSubmissionRepository.save(any(CodeSubmission.class))).thenReturn(submission);
        when(reviewRepository.findByReviewerIdAndStatus(2L, ReviewStatus.SUBMITTED))
                .thenReturn(List.of(testReview));
        when(reputationScoreRepository.findByUserId(2L))
                .thenReturn(Optional.of(ReputationScore.builder()
                        .id(1L).user(reviewer).totalScore(0.0).reviewCount(0).averageAccuracy(0.0).build()));

        ReviewDto result = reviewService.createReview(createDto, 2L);

        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    /** Test that reviewing own code throws exception */
    @Test
    void createReview_OwnSubmission_ThrowsException() {
        ReviewCreateDto createDto = new ReviewCreateDto(1L, "Self review", null);

        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        assertThrows(BadRequestException.class,
                () -> reviewService.createReview(createDto, 1L));
    }

    /** Test retrieving all reviews */
    @Test
    void getAllReviews_ReturnsList() {
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(testReview));

        List<ReviewDto> results = reviewService.getAllReviews();

        assertEquals(1, results.size());
    }

    /** Test retrieving review by ID */
    @Test
    void getReviewById_ExistingId_ReturnsDto() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        ReviewDto result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    /** Test retrieving non-existent review throws exception */
    @Test
    void getReviewById_NonExistent_ThrowsException() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewById(999L));
    }

    /** Test deleting review by non-owner throws exception */
    @Test
    void deleteReview_ByNonOwner_ThrowsException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        assertThrows(BadRequestException.class,
                () -> reviewService.deleteReview(1L, 999L));
    }
}
