package com.rakshan.codereview.controller;

import com.rakshan.codereview.dto.DashboardDto;
import com.rakshan.codereview.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the platform dashboard.
 * Provides aggregate statistics including total submissions, reviews, users,
 * status breakdowns, language distribution, and average scores.
 * Dashboard endpoint is public to allow non-authenticated overview.
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Platform dashboard and statistics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** Get dashboard summary data with aggregate statistics */
    @GetMapping
    @Operation(summary = "Get dashboard data", description = "Retrieve platform summary statistics")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
}
