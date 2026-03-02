package com.rakshan.codereview.model;

import com.rakshan.codereview.model.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a peer review for a code submission.
 * Each review is linked to a specific code submission and the reviewer who wrote it.
 * Reviews contain comments and individual ratings across multiple dimensions (e.g., readability, efficiency).
 * Reviews start as DRAFT and transition to SUBMITTED when the reviewer finalises their feedback.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The code submission being reviewed */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_submission_id", nullable = false)
    private CodeSubmission codeSubmission;

    /** The user who performed the review (reviewer identity is hidden from the author) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    /** Overall comments and feedback from the reviewer */
    @Column(columnDefinition = "TEXT")
    private String comments;

    /** Current status of the review: DRAFT or SUBMITTED */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    /** Individual ratings across each review dimension */
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewRating> ratings = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
