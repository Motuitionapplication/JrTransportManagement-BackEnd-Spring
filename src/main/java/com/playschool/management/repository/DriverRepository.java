package com.playschool.management.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playschool.management.dto.DriverDTO;
import com.playschool.management.entity.Driver;

import jakarta.transaction.Transactional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    
    // Find by authentication credentials
    Optional<Driver> findByUserId(String userId);
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByPhoneNumber(String phoneNumber);
    
    // Find by user credentials for login
    Optional<Driver> findByUserIdOrEmailOrPhoneNumber(String userId, String email, String phoneNumber);
    
    // Find by license number
    @Query("SELECT d FROM Driver d WHERE d.drivingLicense.licenseNumber = :licenseNumber")
    Optional<Driver> findByLicenseNumber(@Param("licenseNumber") String licenseNumber);
    
    // Find drivers by status
    List<Driver> findByStatus(Driver.DriverStatus status);
    
    // Find available drivers
    List<Driver> findByStatusAndAccountStatus(Driver.DriverStatus status, Driver.AccountStatus accountStatus);
    
    // Find drivers by verification status
    List<Driver> findByVerificationStatus(Driver.VerificationStatus verificationStatus);
    
    // Find drivers by account status
    List<Driver> findByAccountStatus(Driver.AccountStatus accountStatus);
    
    // Find drivers by background check status
    List<Driver> findByBackgroundCheckStatus(Driver.BackgroundCheckStatus backgroundCheckStatus);
    
    // Find drivers by license type
    @Query("SELECT d FROM Driver d WHERE d.drivingLicense.licenseType = :licenseType")
    List<Driver> findByLicenseType(@Param("licenseType") Driver.LicenseType licenseType);
    
    // Find drivers with expiring licenses
    @Query("SELECT d FROM Driver d WHERE d.drivingLicense.expiryDate BETWEEN :startDate AND :endDate")
    List<Driver> findDriversWithExpiringLicenses(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    // Find drivers with expiring insurance
    @Query("SELECT d FROM Driver d WHERE d.insurance.expiryDate BETWEEN :startDate AND :endDate")
    List<Driver> findDriversWithExpiringInsurance(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    // Find drivers by assigned vehicle
    @Query("SELECT d FROM Driver d WHERE :vehicleId MEMBER OF d.assignedVehicles")
    List<Driver> findByAssignedVehicle(@Param("vehicleId") String vehicleId);
    
    // Find drivers by current vehicle
    List<Driver> findByCurrentVehicle(String vehicleId);
    
    // Find drivers by city
    @Query("SELECT d FROM Driver d WHERE d.address.city = :city")
    List<Driver> findByCity(@Param("city") String city);
    
    // Find drivers by state
    @Query("SELECT d FROM Driver d WHERE d.address.state = :state")
    List<Driver> findByState(@Param("state") String state);
    
    // Find drivers by rating range
    List<Driver> findByAverageRatingGreaterThanEqual(BigDecimal minRating);
    
    // Find top rated drivers
    @Query("SELECT d FROM Driver d WHERE d.averageRating > 0 ORDER BY d.averageRating DESC")
    List<Driver> findTopRatedDrivers();
    
    // Find drivers by experience
    List<Driver> findByTotalYearsExperienceGreaterThanEqual(Integer minExperience);
    
    // Find drivers with specialization
    @Query("SELECT d FROM Driver d WHERE :specialization MEMBER OF d.specializations")
    List<Driver> findBySpecialization(@Param("specialization") String specialization);
    
    // Find drivers by blood group
    List<Driver> findByBloodGroup(String bloodGroup);
    
    // Find drivers with reward points
    List<Driver> findByCurrentRewardPointsGreaterThan(Integer points);
    
    // Count drivers by status
    long countByStatus(Driver.DriverStatus status);
    
    // Count verified drivers
    long countByVerificationStatus(Driver.VerificationStatus verificationStatus);
    
    // Count active drivers
    long countByAccountStatus(Driver.AccountStatus accountStatus);
    
    // Find recently registered drivers
    @Query("SELECT d FROM Driver d WHERE d.createdAt >= :fromDate ORDER BY d.createdAt DESC")
    List<Driver> findRecentlyRegistered(@Param("fromDate") java.time.LocalDateTime fromDate);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if phone number exists
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Check if user ID exists
    boolean existsByUserId(String userId);
    
    // Check if license number exists
    @Query("SELECT COUNT(d) > 0 FROM Driver d WHERE d.drivingLicense.licenseNumber = :licenseNumber")
    boolean existsByLicenseNumber(@Param("licenseNumber") String licenseNumber);
    
    // Check if identity proof number exists
    @Query("SELECT COUNT(d) > 0 FROM Driver d WHERE d.identityProof.number = :identityNumber")
    boolean existsByIdentityProofNumber(@Param("identityNumber") String identityNumber);

    // Find all drivers by vehicle owner ID
    List<Driver> findByVehicleOwnerId(String ownerId);
    
//    @Query(value = "SELECT * FROM drivers d WHERE d.vehicle_owner_id = :ownerId", nativeQuery = true)
//    List<Driver> findDriversByOwnerIdWithNativeQuery(String ownerId);
//    
    @Modifying
    @Transactional
    @Query("UPDATE Driver d SET " +
           "d.firstName = :#{#dto.firstName}, " +
           "d.lastName = :#{#dto.lastName}, " +
           "d.email = :#{#dto.email}, " +
           "d.phoneNumber = :#{#dto.phoneNumber}, " +
           "d.status = :#{#dto.status} " +
           "WHERE d.id = :id")
    int updateDriverFromDto(@Param("id") String id, @Param("dto") DriverDTO dto);

}

