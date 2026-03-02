package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.CodeSubmissionCreateDto;
import com.rakshan.codereview.dto.CodeSubmissionDto;
import com.rakshan.codereview.exception.BadRequestException;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import com.rakshan.codereview.model.CodeSubmission;
import com.rakshan.codereview.model.User;
import com.rakshan.codereview.model.enums.Language;
import com.rakshan.codereview.model.enums.SubmissionStatus;
import com.rakshan.codereview.repository.CodeSubmissionRepository;
import com.rakshan.codereview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing code submissions.
 * Handles CRUD operations and business logic for creating, updating,
 * retrieving, and deleting code submissions.
 * New submissions are created with PENDING_REVIEW status and can only be modified
 * by their original author before a reviewer is assigned.
 */
@Service
public class CodeSubmissionService {

    private final CodeSubmissionRepository codeSubmissionRepository;
    private final UserRepository userRepository;

    public CodeSubmissionService(CodeSubmissionRepository codeSubmissionRepository,
                                 UserRepository userRepository) {
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.userRepository = userRepository;
    }

    /** Creates a new code submission with PENDING_REVIEW status */
    @Transactional
    public CodeSubmissionDto createSubmission(CodeSubmissionCreateDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        CodeSubmission submission = CodeSubmission.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .code(dto.getCode())
                .language(dto.getLanguage())
                .status(SubmissionStatus.PENDING_REVIEW)
                .user(user)
                .build();

        submission = codeSubmissionRepository.save(submission);
        return mapToDto(submission);
    }

    /** Retrieves all code submissions */
    @Transactional(readOnly = true)
    public List<CodeSubmissionDto> getAllSubmissions() {
        return codeSubmissionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves a single code submission by ID */
    @Transactional(readOnly = true)
    public CodeSubmissionDto getSubmissionById(Long id) {
        CodeSubmission submission = codeSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CodeSubmission", id));
        return mapToDto(submission);
    }

    /** Retrieves all submissions by a specific user */
    @Transactional(readOnly = true)
    public List<CodeSubmissionDto> getSubmissionsByUser(Long userId) {
        return codeSubmissionRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves all submissions by status */
    @Transactional(readOnly = true)
    public List<CodeSubmissionDto> getSubmissionsByStatus(String status) {
        SubmissionStatus submissionStatus = SubmissionStatus.valueOf(status.toUpperCase());
        return codeSubmissionRepository.findByStatus(submissionStatus).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves all submissions by programming language */
    @Transactional(readOnly = true)
    public List<CodeSubmissionDto> getSubmissionsByLanguage(String language) {
        Language lang = Language.valueOf(language.toUpperCase());
        return codeSubmissionRepository.findByLanguage(lang).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Updates an existing code submission - only allowed if status is PENDING_REVIEW */
    @Transactional
    public CodeSubmissionDto updateSubmission(Long id, CodeSubmissionCreateDto dto, Long userId) {
        CodeSubmission submission = codeSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CodeSubmission", id));

        // Only the original author can update their submission
        if (!submission.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only update your own submissions");
        }

        // Cannot update submissions that are already under review or reviewed
        if (submission.getStatus() != SubmissionStatus.PENDING_REVIEW) {
            throw new BadRequestException("Cannot update a submission that is already under review or reviewed");
        }

        submission.setTitle(dto.getTitle());
        submission.setDescription(dto.getDescription());
        submission.setCode(dto.getCode());
        submission.setLanguage(dto.getLanguage());

        submission = codeSubmissionRepository.save(submission);
        return mapToDto(submission);
    }

    /** Deletes a code submission - only allowed by the original author */
    @Transactional
    public void deleteSubmission(Long id, Long userId) {
        CodeSubmission submission = codeSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CodeSubmission", id));

        if (!submission.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own submissions");
        }

        codeSubmissionRepository.delete(submission);
    }

    /** Maps CodeSubmission entity to CodeSubmissionDto */
    private CodeSubmissionDto mapToDto(CodeSubmission submission) {
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setId(submission.getId());
        dto.setTitle(submission.getTitle());
        dto.setDescription(submission.getDescription());
        dto.setCode(submission.getCode());
        dto.setLanguage(submission.getLanguage().name());
        dto.setStatus(submission.getStatus().name());
        dto.setUserId(submission.getUser().getId());
        dto.setUsername(submission.getUser().getUsername());
        dto.setCreatedAt(submission.getCreatedAt());
        dto.setUpdatedAt(submission.getUpdatedAt());

        // Include assigned reviewer info if present
        if (submission.getAssignedReviewer() != null) {
            dto.setAssignedReviewerId(submission.getAssignedReviewer().getId());
            dto.setAssignedReviewerUsername(submission.getAssignedReviewer().getUsername());
        }

        return dto;
    }
}
