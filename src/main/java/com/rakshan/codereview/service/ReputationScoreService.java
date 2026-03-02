package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.ReputationScoreDto;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import com.rakshan.codereview.model.ReputationScore;
import com.rakshan.codereview.repository.ReputationScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing reputation scores and the reviewer leaderboard.
 * Reputation scores are automatically updated when reviews are submitted
 * (handled by ReviewService). This service provides read-only access
 * to reputation data for display in the leaderboard and user profiles.
 */
@Service
public class ReputationScoreService {

    private final ReputationScoreRepository reputationScoreRepository;

    public ReputationScoreService(ReputationScoreRepository reputationScoreRepository) {
        this.reputationScoreRepository = reputationScoreRepository;
    }

    /** Retrieves the leaderboard - all reputation scores sorted by total score descending */
    @Transactional(readOnly = true)
    public List<ReputationScoreDto> getLeaderboard() {
        return reputationScoreRepository.findAllByOrderByTotalScoreDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves the reputation score for a specific user */
    @Transactional(readOnly = true)
    public ReputationScoreDto getReputationByUserId(Long userId) {
        ReputationScore score = reputationScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ReputationScore for user", userId));
        return mapToDto(score);
    }

    /** Maps ReputationScore entity to ReputationScoreDto */
    private ReputationScoreDto mapToDto(ReputationScore score) {
        return new ReputationScoreDto(
                score.getId(),
                score.getUser().getId(),
                score.getUser().getUsername(),
                score.getTotalScore(),
                score.getReviewCount(),
                score.getAverageAccuracy(),
                score.getUpdatedAt()
        );
    }
}
