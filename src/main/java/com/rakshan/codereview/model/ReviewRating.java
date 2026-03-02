package com.rakshan.codereview.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a score given by a reviewer for a specific dimension of a code review.
 * Links a Review to a ReviewDimension with a numeric score.
 * For example, a reviewer might give a score of 4 out of 5 for the "Readability" dimension.
 */
@Entity
@Table(name = "review_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The review this rating belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    /** The dimension being rated (e.g., readability, efficiency) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dimension_id", nullable = false)
    private ReviewDimension dimension;

    /** Numeric score assigned by the reviewer for this dimension */
    @Column(nullable = false)
    private Integer score;
}
