package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.DashboardDto;
import com.rakshan.codereview.model.enums.ReviewStatus;
import com.rakshan.codereview.model.enums.SubmissionStatus;
import com.rakshan.codereview.repository.CodeSubmissionRepository;
import com.rakshan.codereview.repository.ReviewRepository;
import com.rakshan.codereview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for aggregating dashboard statistics.
 * Provides summary counts, submission breakdowns by language,
 * and average review scores for the platform overview dashboard.
 */
@Service
public class DashboardService {

    private final CodeSubmissionRepository codeSubmissionRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public DashboardService(CodeSubmissionRepository codeSubmissionRepository,
                            ReviewRepository reviewRepository,
                            UserRepository userRepository) {
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    /**
     * Compiles all dashboard statistics into a single DTO.
     * Includes total counts, status breakdowns, language distribution, and average scores.
     */
    @Transactional(readOnly = true)
    public DashboardDto getDashboardData() {
        DashboardDto dashboard = new DashboardDto();

        // Total counts for summary cards
        dashboard.setTotalSubmissions(codeSubmissionRepository.count());
        dashboard.setTotalReviews(reviewRepository.countByStatus(ReviewStatus.SUBMITTED));
        dashboard.setTotalUsers(userRepository.count());

        // Submission status breakdown
        dashboard.setPendingReviews(codeSubmissionRepository.countByStatus(SubmissionStatus.PENDING_REVIEW));
        dashboard.setUnderReview(codeSubmissionRepository.countByStatus(SubmissionStatus.UNDER_REVIEW));
        dashboard.setCompletedReviews(codeSubmissionRepository.countByStatus(SubmissionStatus.REVIEWED));

        // Submissions grouped by programming language for the bar chart
        Map<String, Long> languageMap = new LinkedHashMap<>();
        List<Object[]> languageCounts = codeSubmissionRepository.countByLanguageGrouped();
        for (Object[] row : languageCounts) {
            languageMap.put(row[0].toString(), (Long) row[1]);
        }
        dashboard.setSubmissionsByLanguage(languageMap);

        // Average score across all submitted reviews
        Double avgScore = reviewRepository.calculateAverageScore();
        dashboard.setAverageReviewScore(avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0);

        return dashboard;
    }
}
