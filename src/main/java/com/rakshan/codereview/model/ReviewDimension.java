package com.rakshan.codereview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a review dimension (criterion) used to evaluate code submissions.
 * Examples include readability, efficiency, security, and coding style.
 * Each dimension has a name, description, and maximum score that reviewers can assign.
 * Admins can create, update, and delete dimensions to customise the review criteria.
 */
@Entity
@Table(name = "review_dimensions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of the review dimension (e.g., "Readability", "Efficiency") */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** Description explaining what this dimension evaluates */
    @Column(length = 500)
    private String description;

    /** Maximum score a reviewer can assign for this dimension (typically 1-5) */
    @Column(nullable = false)
    private Integer maxScore;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
