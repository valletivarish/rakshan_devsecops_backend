package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.ForecastDto;
import com.rakshan.codereview.model.Review;
import com.rakshan.codereview.model.ReviewRating;
import com.rakshan.codereview.model.enums.ReviewStatus;
import com.rakshan.codereview.repository.ReviewRepository;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ML/Analytics service implementing review quality prediction using Apache Commons Math.
 * Uses SimpleRegression (linear regression) to predict the likely quality score
 * of a code submission based on historical review patterns.
 * The prediction model considers code length and historical score trends
 * to forecast expected review scores for new submissions.
 */
@Service
public class ForecastService {

    private final ReviewRepository reviewRepository;

    public ForecastService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Predicts the expected quality score for a new submission.
     * Uses linear regression on historical review data where:
     * - X axis = code length (number of characters in the submission code)
     * - Y axis = average review score received
     *
     * This allows the system to predict what score a submission of a given length
     * is likely to receive based on historical patterns.
     *
     * @param codeLength the length of the code snippet to predict score for
     * @return ForecastDto with predicted score, trend direction, and confidence
     */
    @Transactional(readOnly = true)
    public ForecastDto predictQualityScore(int codeLength) {
        // Get all submitted reviews for building the regression model
        List<Review> reviews = reviewRepository.findByStatus(ReviewStatus.SUBMITTED);

        // Need at least 3 data points for a meaningful regression
        if (reviews.size() < 3) {
            return new ForecastDto(
                    0.0,
                    "INSUFFICIENT_DATA",
                    0.0,
                    reviews.size(),
                    "Not enough historical review data to make a prediction. At least 3 completed reviews are needed."
            );
        }

        // Build the linear regression model using Apache Commons Math SimpleRegression
        SimpleRegression regression = new SimpleRegression();

        for (Review review : reviews) {
            // X = code length of the reviewed submission
            double submissionCodeLength = review.getCodeSubmission().getCode().length();
            // Y = average score across all dimensions for this review
            double avgScore = review.getRatings().stream()
                    .mapToInt(ReviewRating::getScore)
                    .average()
                    .orElse(0.0);

            regression.addData(submissionCodeLength, avgScore);
        }

        // Predict the score for the given code length
        double predictedScore = regression.predict(codeLength);
        // Clamp the predicted score between 0 and 5 (valid score range)
        predictedScore = Math.max(0.0, Math.min(5.0, predictedScore));
        predictedScore = Math.round(predictedScore * 100.0) / 100.0;

        // Determine trend direction from the regression slope
        double slope = regression.getSlope();
        String trend;
        if (slope > 0.001) {
            trend = "IMPROVING";
        } else if (slope < -0.001) {
            trend = "DECLINING";
        } else {
            trend = "STABLE";
        }

        // R-squared value indicates how well the model fits the data (0 to 1)
        double rSquared = regression.getRSquare();
        double confidence = Double.isNaN(rSquared) ? 0.0 : Math.round(rSquared * 100.0) / 100.0;

        String explanation = String.format(
                "Based on %d historical reviews, submissions with code length around %d characters "
                + "are predicted to receive an average score of %.2f. The trend is %s with %.0f%% confidence.",
                reviews.size(), codeLength, predictedScore, trend.toLowerCase(), confidence * 100);

        return new ForecastDto(predictedScore, trend, confidence, reviews.size(), explanation);
    }

    /**
     * Provides a time-series forecast of review scores over time.
     * Uses sequential review index as X and average score as Y
     * to predict future review quality trends.
     *
     * @param periodsAhead number of future periods to forecast
     * @return ForecastDto with the predicted trend
     */
    @Transactional(readOnly = true)
    public ForecastDto predictScoreTrend(int periodsAhead) {
        List<Review> reviews = reviewRepository.findByStatus(ReviewStatus.SUBMITTED);

        if (reviews.size() < 3) {
            return new ForecastDto(0.0, "INSUFFICIENT_DATA", 0.0, reviews.size(),
                    "Not enough data for trend prediction.");
        }

        SimpleRegression regression = new SimpleRegression();

        // Use review index (chronological order) as X, average score as Y
        for (int i = 0; i < reviews.size(); i++) {
            double avgScore = reviews.get(i).getRatings().stream()
                    .mapToInt(ReviewRating::getScore)
                    .average()
                    .orElse(0.0);
            regression.addData(i, avgScore);
        }

        // Predict score for future period
        double futureIndex = reviews.size() + periodsAhead;
        double predictedScore = Math.max(0.0, Math.min(5.0, regression.predict(futureIndex)));
        predictedScore = Math.round(predictedScore * 100.0) / 100.0;

        double slope = regression.getSlope();
        String trend = slope > 0.001 ? "IMPROVING" : slope < -0.001 ? "DECLINING" : "STABLE";
        double confidence = Double.isNaN(regression.getRSquare()) ? 0.0 : regression.getRSquare();

        String explanation = String.format(
                "Based on %d reviews, the predicted average score in %d periods is %.2f. Trend: %s.",
                reviews.size(), periodsAhead, predictedScore, trend.toLowerCase());

        return new ForecastDto(predictedScore, trend,
                Math.round(confidence * 100.0) / 100.0, reviews.size(), explanation);
    }
}
