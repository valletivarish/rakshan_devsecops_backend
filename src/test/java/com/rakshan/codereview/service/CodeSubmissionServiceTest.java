package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.CodeSubmissionCreateDto;
import com.rakshan.codereview.dto.CodeSubmissionDto;
import com.rakshan.codereview.exception.BadRequestException;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import com.rakshan.codereview.model.CodeSubmission;
import com.rakshan.codereview.model.User;
import com.rakshan.codereview.model.enums.Language;
import com.rakshan.codereview.model.enums.Role;
import com.rakshan.codereview.model.enums.SubmissionStatus;
import com.rakshan.codereview.repository.CodeSubmissionRepository;
import com.rakshan.codereview.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CodeSubmissionService.
 * Tests CRUD operations and business logic validations using Mockito mocks.
 */
@ExtendWith(MockitoExtension.class)
class CodeSubmissionServiceTest {

    @Mock
    private CodeSubmissionRepository codeSubmissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CodeSubmissionService codeSubmissionService;

    private User testUser;
    private CodeSubmission testSubmission;
    private CodeSubmissionCreateDto createDto;

    @BeforeEach
    void setUp() {
        // Set up test data before each test
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashedpassword")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        testSubmission = CodeSubmission.builder()
                .id(1L)
                .title("Test Submission")
                .description("A test code submission")
                .code("public class Test { }")
                .language(Language.JAVA)
                .status(SubmissionStatus.PENDING_REVIEW)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createDto = new CodeSubmissionCreateDto();
        createDto.setTitle("Test Submission");
        createDto.setDescription("A test code submission");
        createDto.setCode("public class Test { }");
        createDto.setLanguage(Language.JAVA);
    }

    /** Test creating a new submission with valid data */
    @Test
    void createSubmission_ValidInput_ReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(codeSubmissionRepository.save(any(CodeSubmission.class))).thenReturn(testSubmission);

        CodeSubmissionDto result = codeSubmissionService.createSubmission(createDto, 1L);

        assertNotNull(result);
        assertEquals("Test Submission", result.getTitle());
        assertEquals("JAVA", result.getLanguage());
        assertEquals("PENDING_REVIEW", result.getStatus());
        verify(codeSubmissionRepository, times(1)).save(any(CodeSubmission.class));
    }

    /** Test creating a submission with non-existent user throws exception */
    @Test
    void createSubmission_UserNotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> codeSubmissionService.createSubmission(createDto, 999L));
    }

    /** Test retrieving all submissions returns correct list */
    @Test
    void getAllSubmissions_ReturnsListOfDtos() {
        when(codeSubmissionRepository.findAll()).thenReturn(Arrays.asList(testSubmission));

        List<CodeSubmissionDto> results = codeSubmissionService.getAllSubmissions();

        assertEquals(1, results.size());
        assertEquals("Test Submission", results.get(0).getTitle());
    }

    /** Test retrieving a submission by ID */
    @Test
    void getSubmissionById_ExistingId_ReturnsDto() {
        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        CodeSubmissionDto result = codeSubmissionService.getSubmissionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    /** Test retrieving a non-existent submission throws exception */
    @Test
    void getSubmissionById_NonExistentId_ThrowsException() {
        when(codeSubmissionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> codeSubmissionService.getSubmissionById(999L));
    }

    /** Test updating a submission by its author */
    @Test
    void updateSubmission_ByAuthor_ReturnsUpdatedDto() {
        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));
        when(codeSubmissionRepository.save(any(CodeSubmission.class))).thenReturn(testSubmission);

        createDto.setTitle("Updated Title");
        CodeSubmissionDto result = codeSubmissionService.updateSubmission(1L, createDto, 1L);

        assertNotNull(result);
        verify(codeSubmissionRepository, times(1)).save(any(CodeSubmission.class));
    }

    /** Test updating a submission by non-author throws exception */
    @Test
    void updateSubmission_ByNonAuthor_ThrowsException() {
        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        assertThrows(BadRequestException.class,
                () -> codeSubmissionService.updateSubmission(1L, createDto, 999L));
    }

    /** Test that updating a reviewed submission throws exception */
    @Test
    void updateSubmission_AlreadyReviewed_ThrowsException() {
        testSubmission.setStatus(SubmissionStatus.REVIEWED);
        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        assertThrows(BadRequestException.class,
                () -> codeSubmissionService.updateSubmission(1L, createDto, 1L));
    }

    /** Test deleting a submission by its author */
    @Test
    void deleteSubmission_ByAuthor_Succeeds() {
        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        codeSubmissionService.deleteSubmission(1L, 1L);

        verify(codeSubmissionRepository, times(1)).delete(testSubmission);
    }

    /** Test deleting a submission by non-author throws exception */
    @Test
    void deleteSubmission_ByNonAuthor_ThrowsException() {
        when(codeSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        assertThrows(BadRequestException.class,
                () -> codeSubmissionService.deleteSubmission(1L, 999L));
    }
}
