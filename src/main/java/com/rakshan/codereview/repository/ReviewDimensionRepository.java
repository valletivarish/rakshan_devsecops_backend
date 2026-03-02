package com.rakshan.codereview.repository;

import com.rakshan.codereview.model.ReviewDimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ReviewDimension entity.
 * Provides lookup by name to check for duplicate dimensions.
 */
@Repository
public interface ReviewDimensionRepository extends JpaRepository<ReviewDimension, Long> {

    /** Find a dimension by its name - used to prevent duplicate dimension names */
    Optional<ReviewDimension> findByName(String name);

    /** Check if a dimension name already exists */
    boolean existsByName(String name);
}
