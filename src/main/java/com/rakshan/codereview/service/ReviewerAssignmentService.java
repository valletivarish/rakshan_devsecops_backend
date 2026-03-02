package com.rakshan.codereview.service;

import com.rakshan.codereview.model.CodeSubmission;
import com.rakshan.codereview.model.User;
import com.rakshan.codereview.model.enums.SubmissionStatus;
import com.rakshan.codereview.repository.CodeSubmissionRepository;
import com.rakshan.codereview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Service implementing the anonymous random reviewer assignment algorithm.
 * When a submission is pending review, this service selects a random user
 * (excluding the submission author) to be the reviewer.
 * The reviewer's identity is hidden from the code author to ensure unbiased reviews.
 */
@Service
public class ReviewerAssignmentService {

    private final CodeSubmissionRepository codeSubmissionRepository;
    private final UserRepository userRepository;
    private final Random random;

    public ReviewerAssignmentService(CodeSubmissionRepository codeSubmissionRepository,
                                     UserRepository userRepository) {
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.userRepository = userRepository;
        this.random = new Random();
    }

    /**
     * Assigns a random reviewer to a pending code submission.
     * The selected reviewer must not be the author of the submission.
     * Updates the submission status from PENDING_REVIEW to UNDER_REVIEW.
     *
     * @return the assigned reviewer, or null if no eligible reviewer is available
     */
    @Transactional
    public User assignReviewer(CodeSubmission submission) {
        // Get all users except the submission author
        List<User> eligibleReviewers = userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(submission.getUser().getId()))
                .toList();

        if (eligibleReviewers.isEmpty()) {
            return null;
        }

        // Select a random reviewer from the eligible pool
        User selectedReviewer = eligibleReviewers.get(random.nextInt(eligibleReviewers.size()));

        // Update the submission with the assigned reviewer and status
        submission.setAssignedReviewer(selectedReviewer);
        submission.setStatus(SubmissionStatus.UNDER_REVIEW);
        codeSubmissionRepository.save(submission);

        return selectedReviewer;
    }
}
