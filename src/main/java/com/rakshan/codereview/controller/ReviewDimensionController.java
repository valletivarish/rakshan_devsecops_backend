package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.ReviewDimensionCreateDto;
import com.rakshan.codereview.dto.ReviewDimensionDto;
import com.rakshan.codereview.service.ReviewDimensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for review dimension (criteria) CRUD operations.
 * Dimensions define the criteria used to evaluate code submissions
 * (e.g., readability, efficiency, security, coding style).
 */
@RestController
@RequestMapping("/api/dimensions")
@Tag(name = "Review Dimensions", description = "CRUD endpoints for review criteria/dimensions")
public class ReviewDimensionController {

    private final ReviewDimensionService reviewDimensionService;

    public ReviewDimensionController(ReviewDimensionService reviewDimensionService) {
        this.reviewDimensionService = reviewDimensionService;
    }

    /** Create a new review dimension */
    @PostMapping
    @Operation(summary = "Create dimension", description = "Create a new review dimension/criterion")
    public ResponseEntity<ReviewDimensionDto> createDimension(
            @Valid @RequestBody ReviewDimensionCreateDto dto) {
        ReviewDimensionDto dimension = reviewDimensionService.createDimension(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dimension);
    }

    /** Get all review dimensions */
    @GetMapping
    @Operation(summary = "Get all dimensions", description = "Retrieve all review dimensions")
    public ResponseEntity<List<ReviewDimensionDto>> getAllDimensions() {
        return ResponseEntity.ok(reviewDimensionService.getAllDimensions());
    }

    /** Get a specific review dimension by ID */
    @GetMapping("/{id}")
    @Operation(summary = "Get dimension by ID", description = "Retrieve a specific review dimension")
    public ResponseEntity<ReviewDimensionDto> getDimensionById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewDimensionService.getDimensionById(id));
    }

    /** Update an existing review dimension */
    @PutMapping("/{id}")
    @Operation(summary = "Update dimension", description = "Update an existing review dimension")
    public ResponseEntity<ReviewDimensionDto> updateDimension(
            @PathVariable Long id,
            @Valid @RequestBody ReviewDimensionCreateDto dto) {
        return ResponseEntity.ok(reviewDimensionService.updateDimension(id, dto));
    }

    /** Delete a review dimension by ID */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dimension", description = "Delete a review dimension")
    public ResponseEntity<Void> deleteDimension(@PathVariable Long id) {
        reviewDimensionService.deleteDimension(id);
        return ResponseEntity.noContent().build();
    }
}
