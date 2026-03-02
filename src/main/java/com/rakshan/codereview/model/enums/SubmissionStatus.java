package com.rakshan.codereview.model.enums;

/**
 * Enumeration representing the lifecycle status of a code submission.
 * PENDING_REVIEW - submission is waiting for a reviewer to be assigned.
 * UNDER_REVIEW - a reviewer has been assigned and is actively reviewing.
 * REVIEWED - the review has been completed and submitted.
 */
public enum SubmissionStatus {
    PENDING_REVIEW,
    UNDER_REVIEW,
    REVIEWED
}
