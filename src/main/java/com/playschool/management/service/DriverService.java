package com.playschool.management.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playschool.management.dto.DriverDTO;
import com.playschool.management.entity.Driver;
import com.playschool.management.repository.DriverRepository;

@Service
@Transactional
public class DriverService {

    private static final Logger log = LoggerFactory.getLogger(DriverService.class);
    
    private final DriverRepository driverRepository;
    
    @Autowired
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

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

    @Transactional(readOnly = true)
    public Optional<Driver> findByEmail(String email) {
        return driverRepository.findByEmail(email);
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
        return driverRepository.findByCurrentVehicle(vehicleId);
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
}
