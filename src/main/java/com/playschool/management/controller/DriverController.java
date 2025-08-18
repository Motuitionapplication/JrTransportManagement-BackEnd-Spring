package com.playschool.management.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playschool.management.dto.DriverDTO;
import com.playschool.management.dto.request.AssignVehicleRequestDTO;
import com.playschool.management.entity.Driver;
import com.playschool.management.service.DriverService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transport/drivers")
@Tag(name = "Driver Management", description = "APIs for managing drivers in the transport system")
// @CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;
    
    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @Operation(summary = "Create a new driver", description = "Register a new driver in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Driver created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Driver already exists")
    })
    public ResponseEntity<Driver> createDriver(
            @Valid @RequestBody Driver driver) {
        Driver savedDriver = driverService.createDriver(driver);
        return new ResponseEntity<>(savedDriver, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all drivers", description = "Retrieve a paginated list of all drivers")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    public ResponseEntity<Page<Driver>> getAllDrivers(
            @Parameter(description = "Pagination information") Pageable pageable) {
        Page<Driver> drivers = driverService.getAllDrivers(pageable);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieve a specific driver by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Driver found"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    public ResponseEntity<Driver> getDriverById(
            @PathVariable @Parameter(description = "Driver ID") String id) {
        Optional<Driver> driver = driverService.getDriverById(id);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update driver", description = "Update an existing driver's basic details (first name, last name, email, phone, and status)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Driver updated successfully", content = @Content(schema = @Schema(implementation = Driver.class))),
        @ApiResponse(responseCode = "404", description = "Driver not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Driver> updateDriver(
            @PathVariable String id,
            @RequestBody DriverDTO driverDTO) {

        Driver updatedDriver = driverService.updateDriver(id, driverDTO);
        return ResponseEntity.ok(updatedDriver);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete driver", description = "Remove a driver from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    public ResponseEntity<Void> deleteDriver(
            @PathVariable @Parameter(description = "Driver ID") String id) {
        try {
            driverService.deleteDriver(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Authentication and Profile endpoints
    @GetMapping("/profile/userId/{userId}")
    @Operation(summary = "Get driver by user ID", description = "Find driver by their user ID")
    public ResponseEntity<Driver> getDriverByUserId(
            @PathVariable @Parameter(description = "User ID") String userId) {
        Optional<Driver> driver = driverService.findByUserId(userId);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/email/{email}")
    @Operation(summary = "Get driver by email", description = "Find driver by their email address")
    public ResponseEntity<Driver> getDriverByEmail(
            @PathVariable @Parameter(description = "Email address") String email) {
        Optional<Driver> driver = driverService.findByEmail(email);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/phone/{phoneNumber}")
    @Operation(summary = "Get driver by phone", description = "Find driver by their phone number")
    public ResponseEntity<Driver> getDriverByPhoneNumber(
            @PathVariable @Parameter(description = "Phone number") String phoneNumber) {
        Optional<Driver> driver = driverService.findByPhoneNumber(phoneNumber);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/license/{licenseNumber}")
    @Operation(summary = "Get driver by license number", description = "Find driver by their license number")
    public ResponseEntity<Driver> getDriverByLicenseNumber(
            @PathVariable @Parameter(description = "License number") String licenseNumber) {
        Optional<Driver> driver = driverService.findByLicenseNumber(licenseNumber);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    // Status and filter endpoints
    @GetMapping("/status/{status}")
    @Operation(summary = "Get drivers by status", description = "Retrieve drivers filtered by their current status")
    public ResponseEntity<List<Driver>> getDriversByStatus(
            @PathVariable @Parameter(description = "Driver status") Driver.DriverStatus status) {
        List<Driver> drivers = driverService.findByStatus(status);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available drivers", description = "Retrieve drivers who are available and have active accounts")
    public ResponseEntity<List<Driver>> getAvailableDrivers() {
        List<Driver> drivers = driverService.findAvailableDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/verification/{verificationStatus}")
    @Operation(summary = "Get drivers by verification status", description = "Filter drivers by their verification status")
    public ResponseEntity<List<Driver>> getDriversByVerificationStatus(
            @PathVariable @Parameter(description = "Verification status") Driver.VerificationStatus verificationStatus) {
        List<Driver> drivers = driverService.findByVerificationStatus(verificationStatus);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/account-status/{accountStatus}")
    @Operation(summary = "Get drivers by account status", description = "Filter drivers by their account status")
    public ResponseEntity<List<Driver>> getDriversByAccountStatus(
            @PathVariable @Parameter(description = "Account status") Driver.AccountStatus accountStatus) {
        List<Driver> drivers = driverService.findByAccountStatus(accountStatus);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/background-check/{backgroundCheckStatus}")
    @Operation(summary = "Get drivers by background check status", description = "Filter drivers by their background check status")
    public ResponseEntity<List<Driver>> getDriversByBackgroundCheckStatus(
            @PathVariable @Parameter(description = "Background check status") Driver.BackgroundCheckStatus backgroundCheckStatus) {
        List<Driver> drivers = driverService.findByBackgroundCheckStatus(backgroundCheckStatus);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/license-type/{licenseType}")
    @Operation(summary = "Get drivers by license type", description = "Filter drivers by their license type")
    public ResponseEntity<List<Driver>> getDriversByLicenseType(
            @PathVariable @Parameter(description = "License type") Driver.LicenseType licenseType) {
        List<Driver> drivers = driverService.findByLicenseType(licenseType);
        return ResponseEntity.ok(drivers);
    }

    // Location-based endpoints
    @GetMapping("/city/{city}")
    @Operation(summary = "Get drivers by city", description = "Find drivers located in a specific city")
    public ResponseEntity<List<Driver>> getDriversByCity(
            @PathVariable @Parameter(description = "City name") String city) {
        List<Driver> drivers = driverService.findByCity(city);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/state/{state}")
    @Operation(summary = "Get drivers by state", description = "Find drivers located in a specific state")
    public ResponseEntity<List<Driver>> getDriversByState(
            @PathVariable @Parameter(description = "State name") String state) {
        List<Driver> drivers = driverService.findByState(state);
        return ResponseEntity.ok(drivers);
    }

    // Rating and experience endpoints
    @GetMapping("/rating/minimum/{minRating}")
    @Operation(summary = "Get drivers by minimum rating", description = "Find drivers with rating above specified minimum")
    public ResponseEntity<List<Driver>> getDriversByMinimumRating(
            @PathVariable @Parameter(description = "Minimum rating") BigDecimal minRating) {
        List<Driver> drivers = driverService.findByMinimumRating(minRating);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated drivers", description = "Retrieve drivers sorted by their rating in descending order")
    public ResponseEntity<List<Driver>> getTopRatedDrivers() {
        List<Driver> drivers = driverService.findTopRatedDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/experience/minimum/{minExperience}")
    @Operation(summary = "Get drivers by minimum experience", description = "Find drivers with experience above specified minimum years")
    public ResponseEntity<List<Driver>> getDriversByMinimumExperience(
            @PathVariable @Parameter(description = "Minimum years of experience") Integer minExperience) {
        List<Driver> drivers = driverService.findByMinimumExperience(minExperience);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Get drivers by specialization", description = "Find drivers with a specific specialization")
    public ResponseEntity<List<Driver>> getDriversBySpecialization(
            @PathVariable @Parameter(description = "Specialization type") String specialization) {
        List<Driver> drivers = driverService.findBySpecialization(specialization);
        return ResponseEntity.ok(drivers);
    }

    // Vehicle assignment endpoints
    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get drivers by assigned vehicle", description = "Find drivers assigned to a specific vehicle")
    public ResponseEntity<List<Driver>> getDriversByAssignedVehicle(
            @PathVariable @Parameter(description = "Vehicle ID") String vehicleId) {
        List<Driver> drivers = driverService.findByAssignedVehicle(vehicleId);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/current-vehicle/{vehicleId}")
    @Operation(summary = "Get drivers by current vehicle", description = "Find drivers currently using a specific vehicle")
    public ResponseEntity<List<Driver>> getDriversByCurrentVehicle(
            @PathVariable @Parameter(description = "Vehicle ID") String vehicleId) {
        List<Driver> drivers = driverService.findByCurrentVehicle(vehicleId);
        return ResponseEntity.ok(drivers);
    }

    // Document expiry alerts
    @GetMapping("/license/expiring")
    @Operation(summary = "Get drivers with expiring licenses", description = "Find drivers whose licenses are expiring within specified date range")
    public ResponseEntity<List<Driver>> getDriversWithExpiringLicenses(
            @RequestParam @Parameter(description = "Start date") LocalDate startDate,
            @RequestParam @Parameter(description = "End date") LocalDate endDate) {
        List<Driver> drivers = driverService.findDriversWithExpiringLicenses(startDate, endDate);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/insurance/expiring")
    @Operation(summary = "Get drivers with expiring insurance", description = "Find drivers whose insurance is expiring within specified date range")
    public ResponseEntity<List<Driver>> getDriversWithExpiringInsurance(
            @RequestParam @Parameter(description = "Start date") LocalDate startDate,
            @RequestParam @Parameter(description = "End date") LocalDate endDate) {
        List<Driver> drivers = driverService.findDriversWithExpiringInsurance(startDate, endDate);
        return ResponseEntity.ok(drivers);
    }

    // Analytics and statistics endpoints
    @GetMapping("/stats/count/status/{status}")
    @Operation(summary = "Count drivers by status", description = "Get the count of drivers with a specific status")
    public ResponseEntity<Long> countDriversByStatus(
            @PathVariable @Parameter(description = "Driver status") Driver.DriverStatus status) {
        long count = driverService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/count/verification/{verificationStatus}")
    @Operation(summary = "Count drivers by verification status", description = "Get the count of drivers with a specific verification status")
    public ResponseEntity<Long> countDriversByVerificationStatus(
            @PathVariable @Parameter(description = "Verification status") Driver.VerificationStatus verificationStatus) {
        long count = driverService.countByVerificationStatus(verificationStatus);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/count/account-status/{accountStatus}")
    @Operation(summary = "Count drivers by account status", description = "Get the count of drivers with a specific account status")
    public ResponseEntity<Long> countDriversByAccountStatus(
            @PathVariable @Parameter(description = "Account status") Driver.AccountStatus accountStatus) {
        long count = driverService.countByAccountStatus(accountStatus);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recently registered drivers", description = "Retrieve drivers registered after a specific date")
    public ResponseEntity<List<Driver>> getRecentlyRegisteredDrivers(
            @RequestParam @Parameter(description = "From date") LocalDateTime fromDate) {
        List<Driver> drivers = driverService.findRecentlyRegistered(fromDate);
        return ResponseEntity.ok(drivers);
    }

    // Additional filter endpoints
    @GetMapping("/blood-group/{bloodGroup}")
    @Operation(summary = "Get drivers by blood group", description = "Find drivers with a specific blood group")
    public ResponseEntity<List<Driver>> getDriversByBloodGroup(
            @PathVariable @Parameter(description = "Blood group") String bloodGroup) {
        List<Driver> drivers = driverService.findByBloodGroup(bloodGroup);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/reward-points/minimum/{points}")
    @Operation(summary = "Get drivers by minimum reward points", description = "Find drivers with reward points above specified minimum")
    public ResponseEntity<List<Driver>> getDriversByMinimumRewardPoints(
            @PathVariable @Parameter(description = "Minimum reward points") Integer points) {
        List<Driver> drivers = driverService.findByMinimumRewardPoints(points);
        return ResponseEntity.ok(drivers);
    }

    // Validation endpoints
    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Check if email exists", description = "Verify if an email address is already registered")
    public ResponseEntity<Boolean> checkEmailExists(
            @PathVariable @Parameter(description = "Email address") String email) {
        boolean exists = driverService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/phone/{phoneNumber}")
    @Operation(summary = "Check if phone number exists", description = "Verify if a phone number is already registered")
    public ResponseEntity<Boolean> checkPhoneNumberExists(
            @PathVariable @Parameter(description = "Phone number") String phoneNumber) {
        boolean exists = driverService.existsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/userId/{userId}")
    @Operation(summary = "Check if user ID exists", description = "Verify if a user ID is already taken")
    public ResponseEntity<Boolean> checkUserIdExists(
            @PathVariable @Parameter(description = "User ID") String userId) {
        boolean exists = driverService.existsByUserId(userId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/license/{licenseNumber}")
    @Operation(summary = "Check if license number exists", description = "Verify if a license number is already registered")
    public ResponseEntity<Boolean> checkLicenseNumberExists(
            @PathVariable @Parameter(description = "License number") String licenseNumber) {
        boolean exists = driverService.existsByLicenseNumber(licenseNumber);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/identity/{identityNumber}")
    @Operation(summary = "Check if identity proof number exists", description = "Verify if an identity proof number is already registered")
    public ResponseEntity<Boolean> checkIdentityProofNumberExists(
            @PathVariable @Parameter(description = "Identity proof number") String identityNumber) {
        boolean exists = driverService.existsByIdentityProofNumber(identityNumber);
        return ResponseEntity.ok(exists);
    }

    // Status update endpoints
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update driver status", description = "Update the status of a driver")
    public ResponseEntity<Driver> updateDriverStatus(
            @PathVariable @Parameter(description = "Driver ID") String id,
            @RequestParam @Parameter(description = "New status") Driver.DriverStatus status) {
        try {
            Driver updatedDriver = driverService.updateDriverStatus(id, status);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/verification-status")
    @Operation(summary = "Update driver verification status", description = "Update the verification status of a driver")
    public ResponseEntity<Driver> updateDriverVerificationStatus(
            @PathVariable @Parameter(description = "Driver ID") String id,
            @RequestParam @Parameter(description = "New verification status") Driver.VerificationStatus verificationStatus) {
        try {
            Driver updatedDriver = driverService.updateDriverVerificationStatus(id, verificationStatus);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/account-status")
    @Operation(summary = "Update driver account status", description = "Update the account status of a driver")
    public ResponseEntity<Driver> updateDriverAccountStatus(
            @PathVariable @Parameter(description = "Driver ID") String id,
            @RequestParam @Parameter(description = "New account status") Driver.AccountStatus accountStatus) {
        try {
            Driver updatedDriver = driverService.updateDriverAccountStatus(id, accountStatus);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    private static final Logger log = LoggerFactory.getLogger(DriverService.class);

    @PatchMapping("/{driverId}/assign-vehicle")
    @Operation(summary = "Assign a vehicle to a driver", description = "Updates both the driver's assigned vehicles and the vehicle's driverId.")
    public ResponseEntity<Driver> assignVehicleToDriver(
            @Parameter(description = "ID of the driver") @PathVariable String driverId,
            @Valid @RequestBody AssignVehicleRequestDTO request) {

        log.info("Received request to assign vehicle {} to driver {}", request.getVehicleId(), driverId);

        try {
            Driver updatedDriver = driverService.assignVehicle(driverId, request.getVehicleId());
            return ResponseEntity.ok(updatedDriver);
        } catch (Exception e) {
            log.error("Error assigning vehicle to driver: {}", e.getMessage());
            // You can add more specific exception handling for 404 Not Found, 409 Conflict, etc.
            return ResponseEntity.badRequest().build();
        }
    }
}
