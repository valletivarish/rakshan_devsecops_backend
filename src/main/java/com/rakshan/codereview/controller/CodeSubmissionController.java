package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.CodeSubmissionCreateDto;
import com.rakshan.codereview.dto.CodeSubmissionDto;
import com.rakshan.codereview.model.User;
import com.rakshan.codereview.service.CodeSubmissionService;
import com.rakshan.codereview.service.ReviewerAssignmentService;
import com.rakshan.codereview.repository.CodeSubmissionRepository;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for code submission CRUD operations.
 * Authenticated users can create, view, update, and delete code submissions.
 * Also provides endpoints for filtering by status and language,
 * and triggering random reviewer assignment.
 */
@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Code Submissions", description = "CRUD endpoints for code submissions")
public class CodeSubmissionController {

    private final CodeSubmissionService codeSubmissionService;
    private final ReviewerAssignmentService reviewerAssignmentService;
    private final CodeSubmissionRepository codeSubmissionRepository;

    public CodeSubmissionController(CodeSubmissionService codeSubmissionService,
                                     ReviewerAssignmentService reviewerAssignmentService,
                                     CodeSubmissionRepository codeSubmissionRepository) {
        this.codeSubmissionService = codeSubmissionService;
        this.reviewerAssignmentService = reviewerAssignmentService;
        this.codeSubmissionRepository = codeSubmissionRepository;
    }

    /** Create a new code submission - validates input and sets status to PENDING_REVIEW */
    @PostMapping
    @Operation(summary = "Create submission", description = "Submit a new code snippet for peer review")
    public ResponseEntity<CodeSubmissionDto> createSubmission(
            @Valid @RequestBody CodeSubmissionCreateDto dto,
            @AuthenticationPrincipal User user) {
        CodeSubmissionDto submission = codeSubmissionService.createSubmission(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    /** Get all code submissions */
    @GetMapping
    @Operation(summary = "Get all submissions", description = "Retrieve all code submissions")
    public ResponseEntity<List<CodeSubmissionDto>> getAllSubmissions() {
        return ResponseEntity.ok(codeSubmissionService.getAllSubmissions());
    }

    /** Get a specific code submission by ID */
    @GetMapping("/{id}")
    @Operation(summary = "Get submission by ID", description = "Retrieve a specific code submission")
    public ResponseEntity<CodeSubmissionDto> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(codeSubmissionService.getSubmissionById(id));
    }

    /** Get all submissions by a specific user */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get submissions by user", description = "Retrieve all submissions by a specific user")
    public ResponseEntity<List<CodeSubmissionDto>> getSubmissionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(codeSubmissionService.getSubmissionsByUser(userId));
    }

    /** Filter submissions by status (PENDING_REVIEW, UNDER_REVIEW, REVIEWED) */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get submissions by status", description = "Filter submissions by their current status")
    public ResponseEntity<List<CodeSubmissionDto>> getSubmissionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(codeSubmissionService.getSubmissionsByStatus(status));
    }

    /** Filter submissions by programming language */
    @GetMapping("/language/{language}")
    @Operation(summary = "Get submissions by language", description = "Filter submissions by programming language")
    public ResponseEntity<List<CodeSubmissionDto>> getSubmissionsByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(codeSubmissionService.getSubmissionsByLanguage(language));
    }

    /** Update an existing code submission - only allowed for PENDING_REVIEW submissions by the author */
    @PutMapping("/{id}")
    @Operation(summary = "Update submission", description = "Update a pending code submission")
    public ResponseEntity<CodeSubmissionDto> updateSubmission(
            @PathVariable Long id,
            @Valid @RequestBody CodeSubmissionCreateDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(codeSubmissionService.updateSubmission(id, dto, user.getId()));
    }

    /** Delete a code submission - only allowed by the original author */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete submission", description = "Delete a code submission")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        codeSubmissionService.deleteSubmission(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    /** Trigger random reviewer assignment for a pending submission */
    @PostMapping("/{id}/assign-reviewer")
    @Operation(summary = "Assign reviewer", description = "Randomly assign a reviewer to a pending submission")
    public ResponseEntity<Map<String, String>> assignReviewer(@PathVariable Long id) {
        var submission = codeSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CodeSubmission", id));

        User reviewer = reviewerAssignmentService.assignReviewer(submission);
        if (reviewer == null) {
            return ResponseEntity.ok(Map.of("message", "No eligible reviewers available"));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Reviewer assigned successfully",
                "reviewerUsername", reviewer.getUsername()));
    }
}
