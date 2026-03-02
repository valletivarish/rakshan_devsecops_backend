package com.rakshan.codereview.repository;

import com.rakshan.codereview.model.Review;
import com.rakshan.codereview.model.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Review entity with custom queries for filtering by reviewer,
 * submission, and calculating aggregate review statistics.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** Find all reviews written by a specific reviewer */
    List<Review> findByReviewerId(Long reviewerId);

    /** Find all reviews for a specific code submission */
    List<Review> findByCodeSubmissionId(Long codeSubmissionId);

    /** Find reviews by status (DRAFT or SUBMITTED) */
    List<Review> findByStatus(ReviewStatus status);

    /** Count total submitted reviews - used for dashboard */
    long countByStatus(ReviewStatus status);

    /** Calculate the average score across all submitted review ratings for dashboard */
    @Query("SELECT AVG(rr.score) FROM ReviewRating rr WHERE rr.review.status = 'SUBMITTED'")
    Double calculateAverageScore();

    /** Find all submitted reviews by a reviewer - used for reputation calculation */
    List<Review> findByReviewerIdAndStatus(Long reviewerId, ReviewStatus status);
}
