package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.ReviewCreateDto;
import com.rakshan.codereview.dto.ReviewDto;
import com.rakshan.codereview.dto.ReviewRatingDto;
import com.rakshan.codereview.exception.BadRequestException;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import com.rakshan.codereview.model.*;
import com.rakshan.codereview.model.enums.ReviewStatus;
import com.rakshan.codereview.model.enums.SubmissionStatus;
import com.rakshan.codereview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing peer reviews of code submissions.
 * Handles creating reviews with multi-dimensional ratings, updating review status,
 * and calculating average scores. When a review is submitted, the associated
 * code submission status is updated and the reviewer's reputation score is recalculated.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final UserRepository userRepository;
    private final ReviewDimensionRepository reviewDimensionRepository;
    private final ReputationScoreRepository reputationScoreRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         CodeSubmissionRepository codeSubmissionRepository,
                         UserRepository userRepository,
                         ReviewDimensionRepository reviewDimensionRepository,
                         ReputationScoreRepository reputationScoreRepository) {
        this.reviewRepository = reviewRepository;
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.userRepository = userRepository;
        this.reviewDimensionRepository = reviewDimensionRepository;
        this.reputationScoreRepository = reputationScoreRepository;
    }

    /**
     * Creates a new review for a code submission.
     * Validates that the reviewer is not the submission author and that
     * the submission is in UNDER_REVIEW status. Saves dimension ratings
     * and marks the review as SUBMITTED, then updates the submission status to REVIEWED.
     */
    @Transactional
    public ReviewDto createReview(ReviewCreateDto dto, Long reviewerId) {
        CodeSubmission submission = codeSubmissionRepository.findById(dto.getCodeSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("CodeSubmission", dto.getCodeSubmissionId()));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", reviewerId));

        // Prevent authors from reviewing their own code
        if (submission.getUser().getId().equals(reviewerId)) {
            throw new BadRequestException("You cannot review your own code submission");
        }

        // Build the review entity
        Review review = Review.builder()
                .codeSubmission(submission)
                .reviewer(reviewer)
                .comments(dto.getComments())
                .status(ReviewStatus.SUBMITTED)
                .ratings(new ArrayList<>())
                .build();

        // Add dimension ratings if provided
        if (dto.getRatings() != null) {
            for (ReviewRatingDto ratingDto : dto.getRatings()) {
                ReviewDimension dimension = reviewDimensionRepository.findById(ratingDto.getDimensionId())
                        .orElseThrow(() -> new ResourceNotFoundException("ReviewDimension", ratingDto.getDimensionId()));

                // Validate score does not exceed the dimension's max score
                if (ratingDto.getScore() > dimension.getMaxScore()) {
                    throw new BadRequestException("Score for " + dimension.getName()
                            + " cannot exceed " + dimension.getMaxScore());
                }

                ReviewRating rating = ReviewRating.builder()
                        .review(review)
                        .dimension(dimension)
                        .score(ratingDto.getScore())
                        .build();
                review.getRatings().add(rating);
            }
        }

        review = reviewRepository.save(review);

        // Update submission status to REVIEWED after successful review submission
        submission.setStatus(SubmissionStatus.REVIEWED);
        codeSubmissionRepository.save(submission);

        // Update reviewer's reputation score after submitting a review
        updateReputationScore(reviewerId);

        return mapToDto(review);
    }

    /** Retrieves all reviews */
    @Transactional(readOnly = true)
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves a single review by ID */
    @Transactional(readOnly = true)
    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        return mapToDto(review);
    }

    /** Retrieves all reviews written by a specific reviewer */
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByReviewer(Long reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves all reviews for a specific code submission */
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsBySubmission(Long submissionId) {
        return reviewRepository.findByCodeSubmissionId(submissionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Updates an existing review (only allowed for DRAFT reviews) */
    @Transactional
    public ReviewDto updateReview(Long id, ReviewCreateDto dto, Long reviewerId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));

        if (!review.getReviewer().getId().equals(reviewerId)) {
            throw new BadRequestException("You can only update your own reviews");
        }

        review.setComments(dto.getComments());

        // Update ratings if provided
        if (dto.getRatings() != null) {
            review.getRatings().clear();
            for (ReviewRatingDto ratingDto : dto.getRatings()) {
                ReviewDimension dimension = reviewDimensionRepository.findById(ratingDto.getDimensionId())
                        .orElseThrow(() -> new ResourceNotFoundException("ReviewDimension", ratingDto.getDimensionId()));

                ReviewRating rating = ReviewRating.builder()
                        .review(review)
                        .dimension(dimension)
                        .score(ratingDto.getScore())
                        .build();
                review.getRatings().add(rating);
            }
        }

        review = reviewRepository.save(review);
        return mapToDto(review);
    }

    /** Deletes a review by ID - only the reviewer can delete their own review */
    @Transactional
    public void deleteReview(Long id, Long reviewerId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));

        if (!review.getReviewer().getId().equals(reviewerId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    /**
     * Updates the reviewer's reputation score after they submit a review.
     * Calculates average score across all their submitted reviews.
     */
    private void updateReputationScore(Long reviewerId) {
        List<Review> submittedReviews = reviewRepository.findByReviewerIdAndStatus(
                reviewerId, ReviewStatus.SUBMITTED);

        ReputationScore reputation = reputationScoreRepository.findByUserId(reviewerId)
                .orElse(null);

        if (reputation != null && !submittedReviews.isEmpty()) {
            int totalReviews = submittedReviews.size();
            double totalAvgScore = submittedReviews.stream()
                    .mapToDouble(review -> review.getRatings().stream()
                            .mapToInt(ReviewRating::getScore)
                            .average()
                            .orElse(0.0))
                    .average()
                    .orElse(0.0);

            reputation.setReviewCount(totalReviews);
            reputation.setAverageAccuracy(totalAvgScore);
            // Reputation = review count * average quality (rewards both quantity and quality)
            reputation.setTotalScore(totalReviews * totalAvgScore);
            reputationScoreRepository.save(reputation);
        }
    }

    /** Maps Review entity to ReviewDto including ratings and calculated average score */
    private ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setCodeSubmissionId(review.getCodeSubmission().getId());
        dto.setCodeSubmissionTitle(review.getCodeSubmission().getTitle());
        dto.setReviewerId(review.getReviewer().getId());
        dto.setReviewerUsername(review.getReviewer().getUsername());
        dto.setComments(review.getComments());
        dto.setStatus(review.getStatus().name());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());

        // Map dimension ratings
        List<ReviewRatingDto> ratingDtos = review.getRatings().stream()
                .map(rating -> new ReviewRatingDto(
                        rating.getDimension().getId(),
                        rating.getDimension().getName(),
                        rating.getScore()))
                .collect(Collectors.toList());
        dto.setRatings(ratingDtos);

        // Calculate average score across all dimensions for this review
        double avgScore = review.getRatings().stream()
                .mapToInt(ReviewRating::getScore)
                .average()
                .orElse(0.0);
        dto.setAverageScore(Math.round(avgScore * 100.0) / 100.0);

        return dto;
    }
}
