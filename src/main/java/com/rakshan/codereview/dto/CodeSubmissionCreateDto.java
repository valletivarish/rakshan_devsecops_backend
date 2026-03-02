package com.rakshan.codereview.dto;

import com.rakshan.codereview.model.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a code submission.
 * Validates title length (max 200), code snippet presence, description length (max 2000),
 * and requires a valid programming language selection from the allowed list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmissionCreateDto {

    /** Title of the code submission - max 200 characters */
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    /** Description of the code - max 2000 characters */
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    /** The actual code snippet - required and must not be blank */
    @NotBlank(message = "Code is required")
    private String code;

    /** Programming language - must be selected from the allowed Language enum values */
    @NotNull(message = "Language is required")
    private Language language;
}
