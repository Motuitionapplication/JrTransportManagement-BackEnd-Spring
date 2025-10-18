package com.playschool.management.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playschool.management.dto.dashboard.DriverBookingSummaryDto;
import com.playschool.management.dto.dashboard.DriverChartDataDto;
import com.playschool.management.dto.dashboard.DriverDashboardStatsDto;
import com.playschool.management.dto.dashboard.DriverMessageSummaryDto;
import com.playschool.management.dto.dashboard.DriverTripSummaryDto;
import com.playschool.management.service.DriverService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/transport/drivers")
@CrossOrigin(
    origins = {"http://localhost:4200", "http://localhost:3000", "https://jr-transport.netlify.app"}, 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = {"*"},
    allowCredentials = "true",
    maxAge = 3600
)
@Tag(name = "Driver Dashboard", description = "Driver-focused dashboard data APIs")
public class DriverDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DriverDashboardController.class);
    private final DriverService driverService;

    @Autowired
    public DriverDashboardController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check for driver dashboard endpoints")
    public ResponseEntity<String> healthCheck() {
        logger.info("🏥 Dashboard: Health check requested");
        return ResponseEntity.ok("Driver Dashboard Controller is healthy and ready!");
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint for frontend connectivity")
    public ResponseEntity<String> testEndpoint() {
        logger.info("🧪 Dashboard: Test endpoint accessed");
        return ResponseEntity.ok("Driver Dashboard API is accessible from frontend!");
    }

    @GetMapping("/dashboard/{userId}/stats")
    @Operation(
        summary = "Driver dashboard statistics",
        description = "Fetch aggregated KPIs required by the driver dashboard header."
    )
    public ResponseEntity<DriverDashboardStatsDto> getDashboardStats(@PathVariable String userId) {
        logger.info("🚗 Dashboard: Fetching stats for driver userId: {}", userId);
        try {
            DriverDashboardStatsDto stats = driverService.getDashboardStats(userId);
            logger.info("✅ Dashboard: Successfully retrieved stats for userId: {}", userId);
            return ResponseEntity.ok(stats);
        } catch (EntityNotFoundException ex) {
            logger.warn("❌ Dashboard: Driver not found for userId: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("💥 Dashboard: Error fetching stats for userId: {}", userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/trips")
    @Operation(
        summary = "Recent driver trips",
        description = "Returns the latest completed trips for the driver timeline widget."
    )
    public ResponseEntity<List<DriverTripSummaryDto>> getRecentTrips(
            @PathVariable String userId,
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        logger.info("🚗 Dashboard: Fetching {} recent trips for driver userId: {}", limit, userId);
        try {
            List<DriverTripSummaryDto> trips = driverService.getRecentTrips(userId, limit);
            logger.info("✅ Dashboard: Retrieved {} trips for userId: {}", trips.size(), userId);
            return ResponseEntity.ok(trips);
        } catch (EntityNotFoundException ex) {
            logger.warn("❌ Dashboard: Driver not found for trips request, userId: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("💥 Dashboard: Error fetching trips for userId: {}", userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/bookings/active")
    @Operation(
        summary = "Active driver bookings",
        description = "Returns active bookings assigned to the driver for quick actions."
    )
    public ResponseEntity<List<DriverBookingSummaryDto>> getActiveBookings(
            @PathVariable String userId,
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        logger.info("🚗 Dashboard: Fetching {} active bookings for driver userId: {}", limit, userId);
        try {
            List<DriverBookingSummaryDto> bookings = driverService.getActiveBookings(userId, limit);
            logger.info("✅ Dashboard: Retrieved {} active bookings for userId: {}", bookings.size(), userId);
            return ResponseEntity.ok(bookings);
        } catch (EntityNotFoundException ex) {
            logger.warn("❌ Dashboard: Driver not found for active bookings request, userId: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("💥 Dashboard: Error fetching active bookings for userId: {}", userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/messages/summary")
    @Operation(
        summary = "Driver message summary",
        description = "Returns a light-weight summary of recent driver conversations."
    )
    public ResponseEntity<List<DriverMessageSummaryDto>> getMessageSummary(
            @PathVariable String userId,
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        logger.info("🚗 Dashboard: Fetching {} message summaries for driver userId: {}", limit, userId);
        try {
            List<DriverMessageSummaryDto> messages = driverService.getMessageSummary(userId, limit);
            logger.info("✅ Dashboard: Retrieved {} message summaries for userId: {}", messages.size(), userId);
            return ResponseEntity.ok(messages);
        } catch (EntityNotFoundException ex) {
            logger.warn("❌ Dashboard: Driver not found for message summary request, userId: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("💥 Dashboard: Error fetching message summary for userId: {}", userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/dashboard/chart")
    @Operation(
        summary = "Driver dashboard chart data",
        description = "Returns earnings chart data for the driver for the last seven days."
    )
    public ResponseEntity<DriverChartDataDto> getChartData(@PathVariable String userId) {
        logger.info("🚗 Dashboard: Fetching chart data for driver userId: {}", userId);
        try {
            DriverChartDataDto chart = driverService.getChartData(userId);
            logger.info("✅ Dashboard: Retrieved chart data with {} data points for userId: {}", 
                       chart.getSeries() != null ? chart.getSeries().size() : 0, userId);
            return ResponseEntity.ok(chart);
        } catch (EntityNotFoundException ex) {
            logger.warn("❌ Dashboard: Driver not found for chart data request, userId: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("💥 Dashboard: Error fetching chart data for userId: {}", userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
