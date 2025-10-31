package com.playschool.management.repository;

import com.playschool.management.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    // Find vehicles by owner (Correct: Spring traverses owner -> id)
    List<Vehicle> findByOwnerId(String ownerId);

//    // Find vehicles by driver
//    List<Vehicle> findByDriverId(String driverId);

    // Find vehicle by vehicle number
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    // Find vehicles by status
    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);

    // Find vehicles by owner and status (Correct: Spring traverses owner -> id)
    List<Vehicle> findByOwnerIdAndStatus(String ownerId, Vehicle.VehicleStatus status);

    // Find vehicles by type
    List<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType);

    // Find vehicles by location (search in current address)
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.currentAddress) LIKE LOWER(CONCAT('%', :city, '%')) AND LOWER(v.currentAddress) LIKE LOWER(CONCAT('%', :state, '%'))")
    List<Vehicle> findByLocationCityAndLocationState(@Param("city") String city, @Param("state") String state);

    // Find vehicles with minimum capacity
    @Query("SELECT v FROM Vehicle v WHERE v.capacity >= :minCapacity")
    List<Vehicle> findByCapacityGreaterThanEqual(@Param("minCapacity") BigDecimal minCapacity);

    // Find verified vehicles
    List<Vehicle> findByIsVerifiedTrue();

    // FIXED: Renamed for clarity to avoid method overloading
    @Query("SELECT v FROM Vehicle v WHERE " +
           "(v.registration.expiryDate <= :beforeDate) OR " +
           "(v.insurance.expiryDate <= :beforeDate) OR " +
           "(v.permit.expiryDate <= :beforeDate) OR " +
           "(v.fitness.expiryDate <= :beforeDate) OR " +
           "(v.pollution.expiryDate <= :beforeDate)")
    List<Vehicle> findVehiclesWithDocumentsExpiringBefore(@Param("beforeDate") LocalDate beforeDate);

    // FIXED: Renamed for clarity to avoid method overloading
    @Query("SELECT v FROM Vehicle v WHERE " +
           "(v.registration.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.insurance.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.permit.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.fitness.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.pollution.expiryDate BETWEEN :startDate AND :endDate)")
    List<Vehicle> findVehiclesWithDocumentsExpiringBetween(@Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    // FIXED: Corrected query to use v.owner.id
    @Query("SELECT v FROM Vehicle v WHERE v.owner.id = :ownerId AND (" +
           "(v.registration.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.insurance.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.permit.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.fitness.expiryDate BETWEEN :startDate AND :endDate) OR " +
           "(v.pollution.expiryDate BETWEEN :startDate AND :endDate))")
    List<Vehicle> findVehiclesByOwnerWithDocumentsExpiringBetween(@Param("ownerId") String ownerId,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);

    // Find active vehicles
    List<Vehicle> findByIsActiveTrue();

    // FIXED: Changed parameter types to BigDecimal for consistency
    @Query("SELECT v FROM Vehicle v WHERE v.capacity BETWEEN :minCapacity AND :maxCapacity")
    List<Vehicle> findByCapacityRange(@Param("minCapacity") BigDecimal minCapacity,
                                      @Param("maxCapacity") BigDecimal maxCapacity);

    // Count vehicles by owner
    long countByOwnerId(String ownerId);

    // Count active vehicles by owner
    long countByOwnerIdAndIsActiveTrue(String ownerId);

    // Find vehicles with location data
    @Query("SELECT v FROM Vehicle v WHERE v.currentLatitude IS NOT NULL AND v.currentLongitude IS NOT NULL")
    List<Vehicle> findVehiclesWithLocation();
}