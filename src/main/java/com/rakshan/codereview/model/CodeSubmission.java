package com.rakshan.codereview.model;

import com.rakshan.codereview.model.enums.Language;
import com.rakshan.codereview.model.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a code submission submitted by a user for peer review.
 * Each submission contains a code snippet with a title, description, and programming language.
 * Submissions go through a lifecycle: PENDING_REVIEW -> UNDER_REVIEW -> REVIEWED.
 * A reviewer is anonymously assigned by the system using a random assignment algorithm.
 */
@Entity
@Table(name = "code_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Title describing the code submission */
    @Column(nullable = false, length = 200)
    private String title;

    /** Detailed description of what the code does or what feedback is sought */
    @Column(length = 2000)
    private String description;

    /** The actual code snippet submitted for review */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    /** Programming language of the submitted code */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    /** Current lifecycle status of the submission */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    /** The user who authored and submitted this code */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The reviewer assigned to this submission (null if not yet assigned) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_reviewer_id")
    private User assignedReviewer;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
