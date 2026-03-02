package com.rakshan.codereview.repository;

import com.rakshan.codereview.model.ReputationScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ReputationScore entity.
 * Provides queries for the leaderboard and individual user reputation lookup.
 */
@Repository
public interface ReputationScoreRepository extends JpaRepository<ReputationScore, Long> {

    /** Find reputation score by user ID - one-to-one relationship */
    Optional<ReputationScore> findByUserId(Long userId);

    /** Get all reputation scores ordered by total score descending - used for leaderboard */
    List<ReputationScore> findAllByOrderByTotalScoreDesc();
}
