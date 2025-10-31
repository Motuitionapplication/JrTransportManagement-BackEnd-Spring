package com.playschool.management.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.playschool.management.dto.DriverDTO;
import com.playschool.management.dto.dashboard.DriverBookingSummaryDto;
import com.playschool.management.dto.dashboard.DriverChartDataDto;
import com.playschool.management.dto.dashboard.DriverDashboardStatsDto;
import com.playschool.management.dto.dashboard.DriverMessageSummaryDto;
import com.playschool.management.dto.dashboard.DriverTripSummaryDto;
import com.playschool.management.dto.request.MinimalDriverRequestDTO;
import com.playschool.management.dto.response.DriverAvatarResponse;
import com.playschool.management.dto.response.DriverProfileSummaryDto;
import com.playschool.management.entity.AssignmentHistory;
import com.playschool.management.entity.Booking;
import com.playschool.management.entity.Driver;
import com.playschool.management.entity.Vehicle;
import com.playschool.management.entity.User;
import com.playschool.management.repository.AssignmentHistoryRepository;
import com.playschool.management.repository.BookingRepository;
import com.playschool.management.repository.DriverRepository;
import com.playschool.management.repository.VehicleRepository;
import com.playschool.management.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class DriverService {

    private static final Logger log = LoggerFactory.getLogger(DriverService.class);
    
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final AssignmentHistoryRepository assignmentHistoryRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public DriverService(DriverRepository driverRepository, VehicleRepository vehicleRepository,
            AssignmentHistoryRepository assignmentHistoryRepository, BookingRepository bookingRepository,
            UserRepository userRepository) { 
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository; 
        this.assignmentHistoryRepository = assignmentHistoryRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }
    public enum DriverStatus { AVAILABLE, ON_TRIP, OFF_DUTY, BREAK }

    // Basic CRUD operations
    public Driver createDriver(Driver driver) {
        log.info("Creating new driver");
        
        // Basic validation - we'll skip the detailed validation for now to avoid getter issues
        // TODO: Add proper validation once Lombok is working correctly
        
        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public Page<Driver> getAllDrivers(Pageable pageable) {
        log.debug("Retrieving all drivers with pagination: {}", pageable);
        return driverRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Driver> getDriverById(String id) {
        log.debug("Finding driver by id: {}", id);
        return driverRepository.findById(id);
    }

    public Driver updateDriver(String id, DriverDTO driverDTO) {
        log.info("Updating driver with id: {}", id);

        // Fetch existing driver
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        // Apply only non-null fields from DTO
        if (driverDTO.getFirstName() != null) {
            existingDriver.setFirstName(driverDTO.getFirstName());
        }
        if (driverDTO.getLastName() != null) {
            existingDriver.setLastName(driverDTO.getLastName());
        }
        if (driverDTO.getEmail() != null) {
            existingDriver.setEmail(driverDTO.getEmail());
        }
        if (driverDTO.getPhoneNumber() != null) {
            existingDriver.setPhoneNumber(driverDTO.getPhoneNumber());
        }
        if (driverDTO.getStatus() != null) {
            try {
                Driver.DriverStatus status = Driver.DriverStatus.valueOf(driverDTO.getStatus());
                existingDriver.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status value: " + driverDTO.getStatus());
            }
        }

        // Save the updated driver
        return driverRepository.save(existingDriver);
    }


    public void deleteDriver(String id) {
        log.info("Deleting driver with id: {}", id);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        driverRepository.delete(driver);
    }

    // Authentication and profile methods
    @Transactional(readOnly = true)
    public Optional<Driver> findByUserId(String userId) {
        return driverRepository.findByUserId(userId);
    }

    public Driver createMinimalDriver(MinimalDriverRequestDTO request) {
        // COPILOT-FIX: backend safeguard to auto-provision drivers missing profile rows
        final String userId = request.getUserId();
        log.info("Auto-creating minimal driver profile for userId={}", userId);

        Optional<Driver> existing = driverRepository.findByUserId(userId);
        if (existing.isPresent()) {
            log.info("Minimal profile request skipped; driver already exists for userId={}.", userId);
            return existing.get();
        }

        Driver driver = new Driver();
        driver.setUserId(userId);

        String firstName = StringUtils.hasText(request.getFirstName()) ? request.getFirstName() : "Driver";
        String lastName = StringUtils.hasText(request.getLastName()) ? request.getLastName() : "Profile";

        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setFatherName(StringUtils.hasText(request.getLastName()) ? request.getLastName() : "Pending Update");
        driver.setEmail(resolveEmailForMinimalProfile(request));
        driver.setPhoneNumber(resolvePhoneForMinimalProfile(request));
        driver.setProfilePhoto(buildAvatarPlaceholder(firstName, lastName));
        driver.setBloodGroup("UNKNOWN");
        driver.setDateOfBirth(LocalDate.of(1990, 1, 1));
        driver.setPassword("AUTO-" + UUID.randomUUID());

        driver.setStatus(Driver.DriverStatus.AVAILABLE);
        driver.setAccountStatus(Driver.AccountStatus.ACTIVE);
        driver.setVerificationStatus(Driver.VerificationStatus.PENDING);
        driver.setBackgroundCheckStatus(Driver.BackgroundCheckStatus.PENDING);

        driver.setWorkingDays(new ArrayList<>());
        driver.setAssignedVehicles(new ArrayList<>());
        driver.setPreviousEmployers(new ArrayList<>());
        driver.setSpecializations(new ArrayList<>());
        driver.setTripHistory(new ArrayList<>());

        driver.setCurrentRewardPoints(0);
        driver.setTotalEarnedPoints(0);
        driver.setRedeemedPoints(0);
        driver.setTotalYearsExperience(0);
        driver.setTotalTripsCompleted(0);

        return driverRepository.save(driver);
    }

    private String resolveEmailForMinimalProfile(MinimalDriverRequestDTO request) {
        if (StringUtils.hasText(request.getEmail())) {
            return request.getEmail();
        }
        String sanitized = request.getUserId().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        if (!StringUtils.hasText(sanitized)) {
            sanitized = UUID.randomUUID().toString().replace("-", "");
        }
        return sanitized + "@autogen.driver.local";
    }

    private String resolvePhoneForMinimalProfile(MinimalDriverRequestDTO request) {
        if (StringUtils.hasText(request.getPhoneNumber())) {
            return request.getPhoneNumber();
        }
        return "AUTO-" + request.getUserId();
    }

    private String buildAvatarPlaceholder(String firstName, String lastName) {
        String initials = (String.valueOf(firstName.charAt(0)) + String.valueOf(lastName.charAt(0))).toUpperCase();
        return "https://ui-avatars.com/api/?background=2563eb&color=fff&name=" + initials;
    }

    @Transactional(readOnly = true)
    public Optional<Driver> findByEmail(String email) {
        return driverRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<DriverProfileSummaryDto> getProfileSummaryForPrincipal(String username) {
        if (!StringUtils.hasText(username)) {
            return Optional.empty();
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("No user found for username={} while resolving driver profile summary", username);
            return Optional.empty();
        }

        User user = userOpt.get();
        String userId = user.getId() != null ? String.valueOf(user.getId()) : null;

        Optional<Driver> driverOpt = Optional.empty();
        if (StringUtils.hasText(userId)) {
            driverOpt = driverRepository.findByUserId(userId);
        }
        if (driverOpt.isEmpty()) {
            driverOpt = driverRepository.findByEmail(user.getEmail());
        }

        if (driverOpt.isEmpty()) {
            log.warn("No driver record tied to userId/email for username={}", username);
            return Optional.empty();
        }

        Driver driver = driverOpt.get();
        String role = user.getRoles().stream()
                .map(roleEntity -> roleEntity.getName())
                .filter(Objects::nonNull)
                .map(Enum::name)
                .findFirst()
                .orElse("ROLE_DRIVER");

        String avatarUrl = StringUtils.hasText(driver.getProfilePhoto()) ? driver.getProfilePhoto() : null;

        DriverProfileSummaryDto summary = new DriverProfileSummaryDto(
                driver.getId(),
                driver.getUserId(),
                driver.getFirstName(),
                driver.getLastName(),
                role,
                user.getEmail(),
                avatarUrl);

        return Optional.of(summary);
    }

    @Transactional(readOnly = true)
    public Optional<Driver> findByPhoneNumber(String phoneNumber) {
        return driverRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    public Optional<Driver> findByLicenseNumber(String licenseNumber) {
        return driverRepository.findByLicenseNumber(licenseNumber);
    }

    // Status-based queries
    @Transactional(readOnly = true)
    public List<Driver> findByStatus(Driver.DriverStatus status) {
        return driverRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Driver> findAvailableDrivers() {
        return driverRepository.findByStatusAndAccountStatus(
                Driver.DriverStatus.AVAILABLE, Driver.AccountStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Driver> findByVerificationStatus(Driver.VerificationStatus verificationStatus) {
        return driverRepository.findByVerificationStatus(verificationStatus);
    }

    @Transactional(readOnly = true)
    public List<Driver> findByAccountStatus(Driver.AccountStatus accountStatus) {
        return driverRepository.findByAccountStatus(accountStatus);
    }

    @Transactional(readOnly = true)
    public List<Driver> findByBackgroundCheckStatus(Driver.BackgroundCheckStatus backgroundCheckStatus) {
        return driverRepository.findByBackgroundCheckStatus(backgroundCheckStatus);
    }

    @Transactional(readOnly = true)
    public List<Driver> findByLicenseType(Driver.LicenseType licenseType) {
        return driverRepository.findByLicenseType(licenseType);
    }

    // Location-based queries
    @Transactional(readOnly = true)
    public List<Driver> findByCity(String city) {
        return driverRepository.findByCity(city);
    }

    @Transactional(readOnly = true)
    public List<Driver> findByState(String state) {
        return driverRepository.findByState(state);
    }

    // Rating and experience queries
    @Transactional(readOnly = true)
    public List<Driver> findByMinimumRating(BigDecimal minRating) {
        return driverRepository.findByAverageRatingGreaterThanEqual(minRating);
    }

    @Transactional(readOnly = true)
    public List<Driver> findTopRatedDrivers() {
        return driverRepository.findTopRatedDrivers();
    }

    @Transactional(readOnly = true)
    public List<Driver> findByMinimumExperience(Integer minExperience) {
        return driverRepository.findByTotalYearsExperienceGreaterThanEqual(minExperience);
    }

    @Transactional(readOnly = true)
    public List<Driver> findBySpecialization(String specialization) {
        return driverRepository.findBySpecialization(specialization);
    }

    // Vehicle assignment queries
    @Transactional(readOnly = true)
    public List<Driver> findByAssignedVehicle(String vehicleId) {
        return driverRepository.findByAssignedVehicle(vehicleId);
    }

    @Transactional(readOnly = true)
    public List<Driver> findByCurrentVehicle(String vehicleId) {
        // 1. Find the Vehicle entity by its ID
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (vehicleOpt.isEmpty()) {
            // If the vehicle doesn't exist, no driver can be assigned to it.
            return Collections.emptyList();
        }

        // 2. Find the active assignment for that specific vehicle
        Optional<AssignmentHistory> activeAssignment =
            assignmentHistoryRepository.findByVehicleAndIsActiveTrue(vehicleOpt.get());

        // 3. If an active assignment is found, return its driver in a list.
        //    Otherwise, return an empty list. This matches the original method's return type.
        return activeAssignment
            .map(assignment -> Collections.singletonList(assignment.getDriver()))
            .orElse(Collections.emptyList());
    }

    // Document expiry alerts
    @Transactional(readOnly = true)
    public List<Driver> findDriversWithExpiringLicenses(LocalDate startDate, LocalDate endDate) {
        return driverRepository.findDriversWithExpiringLicenses(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Driver> findDriversWithExpiringInsurance(LocalDate startDate, LocalDate endDate) {
        return driverRepository.findDriversWithExpiringInsurance(startDate, endDate);
    }

    // Analytics and statistics
    @Transactional(readOnly = true)
    public long countByStatus(Driver.DriverStatus status) {
        return driverRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByVerificationStatus(Driver.VerificationStatus verificationStatus) {
        return driverRepository.countByVerificationStatus(verificationStatus);
    }

    @Transactional(readOnly = true)
    public long countByAccountStatus(Driver.AccountStatus accountStatus) {
        return driverRepository.countByAccountStatus(accountStatus);
    }

    @Transactional(readOnly = true)
    public List<Driver> findRecentlyRegistered(LocalDateTime fromDate) {
        return driverRepository.findRecentlyRegistered(fromDate);
    }

    // Additional filters
    @Transactional(readOnly = true)
    public List<Driver> findByBloodGroup(String bloodGroup) {
        return driverRepository.findByBloodGroup(bloodGroup);
    }

    @Transactional(readOnly = true)
    public List<Driver> findByMinimumRewardPoints(Integer points) {
        return driverRepository.findByCurrentRewardPointsGreaterThan(points);
    }

    // Validation methods
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return driverRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return driverRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(String userId) {
        return driverRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean existsByLicenseNumber(String licenseNumber) {
        return driverRepository.existsByLicenseNumber(licenseNumber);
    }

    @Transactional(readOnly = true)
    public boolean existsByIdentityProofNumber(String identityNumber) {
        return driverRepository.existsByIdentityProofNumber(identityNumber);
    }

    // Status update methods
    public Driver updateDriverStatus(String id, Driver.DriverStatus status) {
        log.info("Updating driver status for id: {} to status: {}", id, status);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        // Use reflection or manual field setting if Lombok setters don't work
        try {
            driver.getClass().getMethod("setStatus", Driver.DriverStatus.class).invoke(driver, status);
        } catch (Exception e) {
            throw new RuntimeException("Unable to update driver status", e);
        }
        return driverRepository.save(driver);
    }

    public Driver updateDriverVerificationStatus(String id, Driver.VerificationStatus verificationStatus) {
        log.info("Updating driver verification status for id: {} to status: {}", id, verificationStatus);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        try {
            driver.getClass().getMethod("setVerificationStatus", Driver.VerificationStatus.class).invoke(driver, verificationStatus);
        } catch (Exception e) {
            throw new RuntimeException("Unable to update driver verification status", e);
        }
        return driverRepository.save(driver);
    }

    public Driver updateDriverAccountStatus(String id, Driver.AccountStatus accountStatus) {
        log.info("Updating driver account status for id: {} to status: {}", id, accountStatus);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        try {
            driver.getClass().getMethod("setAccountStatus", Driver.AccountStatus.class).invoke(driver, accountStatus);
        } catch (Exception e) {
            throw new RuntimeException("Unable to update driver account status", e);
        }
        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public DriverDashboardStatsDto getDashboardStats(String userId) {
        log.info("🚗 DriverService: Fetching dashboard stats for userId: {}", userId);
        Driver driver = driverRepository.findByUserId(userId)
            .orElseThrow(() -> {
                log.warn("❌ DriverService: No driver found with userId: {}", userId);
                return new EntityNotFoundException("Driver not found with userId: " + userId);
            });

        List<Booking> driverBookings = bookingRepository.findByDriverId(driver.getId());
        log.info("📊 DriverService: Found {} bookings for driver {}", driverBookings.size(), userId);

    LocalDate today = LocalDate.now();

    BigDecimal totalEarnings = driverBookings.stream()
        .filter(booking -> booking.getStatus() == Booking.BookingStatus.DELIVERED)
        .map(this::extractFinalAmount)
        .filter(java.util.Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal todaysEarnings = driverBookings.stream()
        .filter(booking -> booking.getStatus() == Booking.BookingStatus.DELIVERED)
        .map(booking -> {
            BigDecimal amount = extractFinalAmount(booking);
            LocalDateTime deliveredAt = resolveDeliveredAt(booking);
            if (amount != null && deliveredAt != null && deliveredAt.toLocalDate().isEqual(today)) {
                return amount;
            }
            return BigDecimal.ZERO;
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    int tripsToday = (int) driverBookings.stream()
        .filter(booking -> booking.getScheduledPickupDate() != null
            && booking.getScheduledPickupDate().toLocalDate().isEqual(today))
        .count();

    long activeBookings = driverBookings.stream()
        .filter(booking -> booking.getStatus() != null)
        .filter(booking -> EnumSet.of(
            Booking.BookingStatus.CONFIRMED,
            Booking.BookingStatus.ASSIGNED,
            Booking.BookingStatus.PICKED_UP,
            Booking.BookingStatus.IN_TRANSIT)
            .contains(booking.getStatus()))
        .count();

    int unreadMessages = 3; // TODO wire unread count from BookingMessageRepository when conversation tracking is implemented

    return new DriverDashboardStatsDto(
        totalEarnings.doubleValue(),
        todaysEarnings.doubleValue(),
        tripsToday,
        (int) activeBookings,
        unreadMessages);
    }

    @Transactional(readOnly = true)
    public List<DriverTripSummaryDto> getRecentTrips(String userId, int limit) {
    Driver driver = driverRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("Driver not found with userId: " + userId));

    int effectiveLimit = limit > 0 ? limit : 5;

    List<Booking> completedBookings = bookingRepository.findCompletedBookingsByDriver(driver.getId());
    log.info("📊 DriverService: Found {} completed bookings for driver {}", completedBookings.size(), userId);

    return completedBookings.stream()
        .sorted((left, right) -> {
            LocalDateTime leftDate = resolvePickupTime(left);
            LocalDateTime rightDate = resolvePickupTime(right);
            if (leftDate == null && rightDate == null) {
                return 0;
            }
            if (leftDate == null) {
                return 1;
            }
            if (rightDate == null) {
                return -1;
            }
            return rightDate.compareTo(leftDate);
        })
        .limit(effectiveLimit)
        .map(booking -> {
            String bookingId = booking.getId() != null ? booking.getId().toString() : "N/A";
            String pickupCity = resolveCity(booking.getPickupAddress(), "Pickup TBD");
            String dropCity = resolveCity(booking.getDeliveryAddress(), "Destination TBD");
            LocalDateTime pickupTime = resolvePickupTimeWithFallback(booking);
            String status = booking.getStatus() != null ? booking.getStatus().name() : "PENDING";
            BigDecimal finalAmount = extractFinalAmount(booking);
            double amount = finalAmount != null ? finalAmount.doubleValue() : 0.0;
            return new DriverTripSummaryDto(bookingId, pickupCity, dropCity, pickupTime, status, amount);
        })
        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverBookingSummaryDto> getActiveBookings(String userId, int limit) {
    Driver driver = driverRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("Driver not found with userId: " + userId));

    int effectiveLimit = limit > 0 ? limit : 5;

    List<Booking> activeBookings = bookingRepository.findActiveBookings().stream()
        .filter(booking -> driver.getId().equals(booking.getDriverId()))
        .collect(Collectors.toList());
    
    log.info("📊 DriverService: Found {} active bookings for driver {}", activeBookings.size(), userId);

    return activeBookings.stream()
        .sorted((left, right) -> {
            LocalDateTime leftDate = left.getScheduledPickupDate();
            LocalDateTime rightDate = right.getScheduledPickupDate();
            if (leftDate == null && rightDate == null) {
            return 0;
            }
            if (leftDate == null) {
            return 1;
            }
            if (rightDate == null) {
            return -1;
            }
            return leftDate.compareTo(rightDate);
        })
        .limit(effectiveLimit)
        .map(booking -> new DriverBookingSummaryDto(
            booking.getId(),
            resolveCustomerName(booking),
            booking.getScheduledPickupDate() != null ? booking.getScheduledPickupDate() : LocalDateTime.now(),
            resolveCity(booking.getDeliveryAddress(), "Destination TBD"),
            booking.getStatus() != null ? booking.getStatus().name() : Booking.BookingStatus.CONFIRMED.name()))
        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverMessageSummaryDto> getMessageSummary(String userId, int limit) {
    driverRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("Driver not found with userId: " + userId));

    // TODO: Implement real message repository integration
    log.info("📊 DriverService: Message summary not yet implemented for driver {}", userId);
    
    // Return empty list until BookingMessageRepository is implemented
    return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public DriverChartDataDto getChartData(String userId) {
    Driver driver = driverRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("Driver not found with userId: " + userId));

    List<Booking> completedBookings = bookingRepository.findCompletedBookingsByDriver(driver.getId());

    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");
    List<String> labels = new ArrayList<>();
    List<Double> series = new ArrayList<>();

    for (int i = 6; i >= 0; i--) {
        LocalDate day = today.minusDays(i);
        labels.add(day.format(formatter));

        double totalForDay = completedBookings.stream()
            .filter(booking -> {
            LocalDateTime deliveredAt = booking.getActualDeliveryTime() != null
                ? booking.getActualDeliveryTime()
                : booking.getUpdatedAt();
            return deliveredAt != null && deliveredAt.toLocalDate().isEqual(day);
            })
            .map(Booking::getPricing)
            .filter(java.util.Objects::nonNull)
            .map(pricing -> pricing.getFinalAmount())
            .filter(java.util.Objects::nonNull)
            .mapToDouble(BigDecimal::doubleValue)
            .sum();

        series.add(totalForDay);
    }

    log.info("📊 DriverService: Generated chart with {} data points for driver {}", series.size(), userId);

    return new DriverChartDataDto(labels, series);
    }

    @Transactional
    public Driver assignVehicle(String driverId, String vehicleId) {
        // 1. Fetch the core entities
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with ID: " + driverId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleId));

        // 2. Check if the vehicle or driver already has an ACTIVE assignment
        if (assignmentHistoryRepository.findByVehicleAndIsActiveTrue(vehicle).isPresent()) {
            throw new IllegalStateException("Vehicle " + vehicleId + " is already actively assigned.");
        }
        if (assignmentHistoryRepository.findByDriverAndIsActiveTrue(driver).isPresent()) {
            throw new IllegalStateException("Driver " + driverId + " is already on an active trip.");
        }

        // 3. Create the new historical assignment record
        AssignmentHistory newAssignment = new AssignmentHistory();
        newAssignment.setVehicle(vehicle);
        newAssignment.setDriver(driver);
        newAssignment.setAssignmentStartDate(LocalDate.now());
        newAssignment.setAssignmentEndDate(null); // End date is null because it's active
        newAssignment.setActive(true);
        assignmentHistoryRepository.save(newAssignment);

        // 4. Update the status of the vehicle and driver
        vehicle.setStatus(Vehicle.VehicleStatus.IN_TRANSIT);
        vehicleRepository.save(vehicle);

        driver.setStatus(Driver.DriverStatus.ON_TRIP);
        return driverRepository.save(driver);
    }
    @Transactional
    public Driver unassignVehicle(String driverId) {
        // 1. Find the driver
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found with id " + driverId));

        // 2. Find the driver's CURRENT ACTIVE assignment from the history table
        AssignmentHistory activeAssignment = assignmentHistoryRepository.findByDriverAndIsActiveTrue(driver)
            .orElseThrow(() -> new IllegalStateException("Driver " + driverId + " does not have an active vehicle assignment to unassign."));
        
        // 3. Get the vehicle from the assignment record
        Vehicle vehicle = activeAssignment.getVehicle();
        
        // 4. End the assignment period
        activeAssignment.setAssignmentEndDate(LocalDate.now());
        activeAssignment.setActive(false);
        assignmentHistoryRepository.save(activeAssignment);

        // 5. Update the status of the vehicle and driver to AVAILABLE
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        driver.setStatus(Driver.DriverStatus.AVAILABLE);
        return driverRepository.save(driver);
    }
    
    @Transactional
    public Driver deactivateDriver(String driverId) {
        // 1. Find the driver
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found with id " + driverId));

        // 2. CRUCIAL: Check if the driver is on an active trip
        if (assignmentHistoryRepository.findByDriverAndIsActiveTrue(driver).isPresent()) {
            throw new IllegalStateException("Cannot deactivate a driver who is on an active trip. Please unassign the vehicle first.");
        }

        // 3. Update the driver's status
        driver.setStatus(Driver.DriverStatus.INACTIVE);

        // 4. Save and return the updated driver
        return driverRepository.save(driver);
    }

    private BigDecimal extractFinalAmount(Booking booking) {
        if (booking == null) {
            return null;
        }
        Booking.PricingDetails pricing = booking.getPricing();
        return pricing != null ? pricing.getFinalAmount() : null;
    }

    private LocalDateTime resolveDeliveredAt(Booking booking) {
        if (booking == null) {
            return null;
        }
        if (booking.getActualDeliveryTime() != null) {
            return booking.getActualDeliveryTime();
        }
        return booking.getUpdatedAt();
    }

    private LocalDateTime resolvePickupTime(Booking booking) {
        if (booking == null) {
            return null;
        }
        if (booking.getScheduledPickupDate() != null) {
            return booking.getScheduledPickupDate();
        }
        return booking.getActualPickupTime();
    }

    private LocalDateTime resolvePickupTimeWithFallback(Booking booking) {
        LocalDateTime pickup = resolvePickupTime(booking);
        return pickup != null ? pickup : LocalDateTime.now();
    }

    private String resolveCity(Booking.BookingAddress address, String fallback) {
        if (address != null && address.getCity() != null && !address.getCity().isBlank()) {
            return address.getCity();
        }
        return fallback;
    }

    private String resolveCustomerName(Booking booking) {
        if (booking == null) {
            return "Valued Customer";
        }
        Booking.ContactPerson contactPerson = booking.getPickupContact();
        if (contactPerson != null && contactPerson.getName() != null && !contactPerson.getName().isBlank()) {
            return contactPerson.getName();
        }
        return "Valued Customer";
    }

    /**
     * Accepts an image file, validates size/type, stores as data URL in profilePhoto, and returns the URL.
     */
    @Transactional
    public DriverAvatarResponse uploadProfilePhoto(String driverId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Only PNG or JPEG files are allowed");
        }

        long maxBytes = 2L * 1024L * 1024L; // 2MB
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("File size exceeds 2MB limit");
        }

        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));

        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            String dataUrl = "data:" + contentType + ";base64," + base64;
            driver.setProfilePhoto(dataUrl);
            driverRepository.save(driver);
            log.info("Updated profile photo for driver {} ({} bytes)", driverId, file.getSize());
            return new DriverAvatarResponse(dataUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process uploaded image", e);
        }
    }
}
