package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.ReviewDimensionCreateDto;
import com.rakshan.codereview.dto.ReviewDimensionDto;
import com.rakshan.codereview.exception.BadRequestException;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import com.rakshan.codereview.model.ReviewDimension;
import com.rakshan.codereview.repository.ReviewDimensionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing review dimensions (criteria used to evaluate code).
 * Provides CRUD operations for dimensions like readability, efficiency, security, and style.
 * Admins use these to customise the criteria by which code submissions are evaluated.
 */
@Service
public class ReviewDimensionService {

    private final ReviewDimensionRepository reviewDimensionRepository;

    public ReviewDimensionService(ReviewDimensionRepository reviewDimensionRepository) {
        this.reviewDimensionRepository = reviewDimensionRepository;
    }

    /** Creates a new review dimension after checking for name uniqueness */
    @Transactional
    public ReviewDimensionDto createDimension(ReviewDimensionCreateDto dto) {
        if (reviewDimensionRepository.existsByName(dto.getName())) {
            throw new BadRequestException("A dimension with name '" + dto.getName() + "' already exists");
        }

        ReviewDimension dimension = ReviewDimension.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .maxScore(dto.getMaxScore())
                .build();

        dimension = reviewDimensionRepository.save(dimension);
        return mapToDto(dimension);
    }

    /** Retrieves all review dimensions */
    @Transactional(readOnly = true)
    public List<ReviewDimensionDto> getAllDimensions() {
        return reviewDimensionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves a single review dimension by ID */
    @Transactional(readOnly = true)
    public ReviewDimensionDto getDimensionById(Long id) {
        ReviewDimension dimension = reviewDimensionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReviewDimension", id));
        return mapToDto(dimension);
    }

    /** Updates an existing review dimension */
    @Transactional
    public ReviewDimensionDto updateDimension(Long id, ReviewDimensionCreateDto dto) {
        ReviewDimension dimension = reviewDimensionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReviewDimension", id));

        // Check if the new name conflicts with another existing dimension
        reviewDimensionRepository.findByName(dto.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException("A dimension with name '" + dto.getName() + "' already exists");
                    }
                });

        dimension.setName(dto.getName());
        dimension.setDescription(dto.getDescription());
        dimension.setMaxScore(dto.getMaxScore());

        dimension = reviewDimensionRepository.save(dimension);
        return mapToDto(dimension);
    }

    /** Deletes a review dimension by ID */
    @Transactional
    public void deleteDimension(Long id) {
        if (!reviewDimensionRepository.existsById(id)) {
            throw new ResourceNotFoundException("ReviewDimension", id);
        }
        reviewDimensionRepository.deleteById(id);
    }

    /** Maps ReviewDimension entity to ReviewDimensionDto */
    private ReviewDimensionDto mapToDto(ReviewDimension dimension) {
        return new ReviewDimensionDto(
                dimension.getId(),
                dimension.getName(),
                dimension.getDescription(),
                dimension.getMaxScore(),
                dimension.getCreatedAt()
        );
    }
}
