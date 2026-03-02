package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.ReviewCreateDto;
import com.rakshan.codereview.dto.ReviewDto;
import com.rakshan.codereview.model.User;
import com.rakshan.codereview.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for review CRUD operations.
 * Allows authenticated users to create, view, update, and delete reviews.
 * Reviews include multi-dimensional ratings across configured review dimensions.
 */
@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "CRUD endpoints for peer code reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** Create a new review with dimension ratings for a code submission */
    @PostMapping
    @Operation(summary = "Create review", description = "Submit a review with dimension ratings for a code submission")
    public ResponseEntity<ReviewDto> createReview(
            @Valid @RequestBody ReviewCreateDto dto,
            @AuthenticationPrincipal User user) {
        ReviewDto review = reviewService.createReview(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    /** Get all reviews */
    @GetMapping
    @Operation(summary = "Get all reviews", description = "Retrieve all peer reviews")
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    /** Get a specific review by ID */
    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Retrieve a specific review")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    /** Get all reviews by a specific reviewer */
    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Get reviews by reviewer", description = "Retrieve all reviews by a specific reviewer")
    public ResponseEntity<List<ReviewDto>> getReviewsByReviewer(@PathVariable Long reviewerId) {
        return ResponseEntity.ok(reviewService.getReviewsByReviewer(reviewerId));
    }

    /** Get all reviews for a specific code submission */
    @GetMapping("/submission/{submissionId}")
    @Operation(summary = "Get reviews by submission", description = "Retrieve all reviews for a specific submission")
    public ResponseEntity<List<ReviewDto>> getReviewsBySubmission(@PathVariable Long submissionId) {
        return ResponseEntity.ok(reviewService.getReviewsBySubmission(submissionId));
    }

    /** Update an existing review */
    @PutMapping("/{id}")
    @Operation(summary = "Update review", description = "Update an existing review")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewCreateDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.updateReview(id, dto, user.getId()));
    }

    /** Delete a review - only the reviewer can delete their own review */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review", description = "Delete a review")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        reviewService.deleteReview(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
