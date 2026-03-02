package com.rakshan.codereview.repository;

import com.rakshan.codereview.model.ReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ReviewRating entity.
 * Provides queries for aggregating scores across reviews and dimensions.
 */
@Repository
public interface ReviewRatingRepository extends JpaRepository<ReviewRating, Long> {

    /** Find all ratings for a specific review */
    List<ReviewRating> findByReviewId(Long reviewId);

    /** Calculate average score per dimension across all submitted reviews - used for analytics */
    @Query("SELECT rr.dimension.name, AVG(rr.score) FROM ReviewRating rr " +
           "WHERE rr.review.status = 'SUBMITTED' GROUP BY rr.dimension.name")
    List<Object[]> averageScoreByDimension();

    /** Get all ratings for reviews of submissions in a specific language - used for ML prediction */
    @Query("SELECT rr FROM ReviewRating rr WHERE rr.review.codeSubmission.language = :language " +
           "AND rr.review.status = 'SUBMITTED'")
    List<ReviewRating> findBySubmissionLanguage(String language);
}
