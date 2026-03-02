package com.rakshan.codereview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a user's accumulated reputation score as a reviewer.
 * Reputation is calculated based on the quality and consistency of reviews submitted.
 * Higher reputation scores indicate more reliable and thorough reviewers.
 * Each user has exactly one ReputationScore record (one-to-one relationship).
 */
@Entity
@Table(name = "reputation_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReputationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user whose reputation is tracked */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Cumulative reputation score based on review quality */
    @Column(nullable = false)
    private Double totalScore;

    /** Total number of reviews submitted by this user */
    @Column(nullable = false)
    private Integer reviewCount;

    /** Average accuracy/quality rating of reviews (0.0 to 5.0 scale) */
    @Column(nullable = false)
    private Double averageAccuracy;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
