package com.rakshan.codereview.repository;

import com.rakshan.codereview.model.CodeSubmission;
import com.rakshan.codereview.model.enums.Language;
import com.rakshan.codereview.model.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CodeSubmission entity with custom queries for filtering,
 * dashboard statistics, and reviewer assignment logic.
 */
@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {

    /** Find all submissions by a specific user (author) */
    List<CodeSubmission> findByUserId(Long userId);

    /** Find all submissions assigned to a specific reviewer */
    List<CodeSubmission> findByAssignedReviewerId(Long reviewerId);

    /** Find all submissions with a specific status */
    List<CodeSubmission> findByStatus(SubmissionStatus status);

    /** Find all submissions in a specific programming language */
    List<CodeSubmission> findByLanguage(Language language);

    /** Count submissions by status - used for dashboard statistics */
    long countByStatus(SubmissionStatus status);

    /** Count submissions grouped by language for dashboard chart data */
    @Query("SELECT cs.language, COUNT(cs) FROM CodeSubmission cs GROUP BY cs.language")
    List<Object[]> countByLanguageGrouped();

    /** Find submissions pending review that are not authored by the given user (for reviewer assignment) */
    @Query("SELECT cs FROM CodeSubmission cs WHERE cs.status = 'PENDING_REVIEW' AND cs.user.id != :userId")
    List<CodeSubmission> findPendingSubmissionsExcludingUser(Long userId);
}
