package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.ForecastDto;
import com.rakshan.codereview.service.ForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for ML-based review quality predictions.
 * Uses Apache Commons Math SimpleRegression to predict expected review scores
 * based on historical review data and code characteristics.
 */
@RestController
@RequestMapping("/api/forecast")
@Tag(name = "Forecast", description = "ML-based review quality prediction endpoints")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    /**
     * Predict the expected quality score for a submission based on code length.
     * Uses linear regression on historical review data.
     */
    @GetMapping("/predict")
    @Operation(summary = "Predict quality score",
            description = "Predict expected review score for a submission based on code length")
    public ResponseEntity<ForecastDto> predictQualityScore(
            @RequestParam(defaultValue = "500") int codeLength) {
        return ResponseEntity.ok(forecastService.predictQualityScore(codeLength));
    }

    /**
     * Predict the trend of review scores over time.
     * Forecasts the average review score for future periods.
     */
    @GetMapping("/trend")
    @Operation(summary = "Predict score trend",
            description = "Forecast review score trends for future periods")
    public ResponseEntity<ForecastDto> predictScoreTrend(
            @RequestParam(defaultValue = "5") int periodsAhead) {
        return ResponseEntity.ok(forecastService.predictScoreTrend(periodsAhead));
    }

    /** Health check endpoint - publicly accessible */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verify the forecast service is operational")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Forecast service is running");
    }
}
