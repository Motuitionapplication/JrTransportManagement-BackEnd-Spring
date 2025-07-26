package com.playschool.management.service;

import com.playschool.management.entity.Vehicle;
import com.playschool.management.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class VehicleService {
    
    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);
    private final VehicleRepository vehicleRepository;
    
    // Explicit constructor
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    
    // Create or update vehicle
    public Vehicle saveVehicle(Vehicle vehicle) {
        log.info("Saving vehicle with ID: {}", vehicle.getId());
        
        // Set creation time if new vehicle
        if (vehicle.getCreatedAt() == null) {
            vehicle.setCreatedAt(LocalDateTime.now());
        }
        vehicle.setUpdatedAt(LocalDateTime.now());
        
        return vehicleRepository.save(vehicle);
    }
    
    // Find vehicle by ID
    @Transactional(readOnly = true)
    public Optional<Vehicle> findVehicleById(String vehicleId) {
        log.info("Finding vehicle by ID: {}", vehicleId);
        return vehicleRepository.findById(vehicleId);
    }
    
    // Get all vehicles
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        log.info("Fetching all vehicles");
        return vehicleRepository.findAll();
    }
    
    // Find vehicles by owner
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByOwner(String ownerId) {
        log.info("Fetching vehicles for owner: {}", ownerId);
        return vehicleRepository.findByOwnerId(ownerId);
    }
    
    // Find vehicles by type
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByType(String vehicleType) {
        log.info("Fetching vehicles of type: {}", vehicleType);
        Vehicle.VehicleType type = Vehicle.VehicleType.valueOf(vehicleType.toUpperCase());
        return vehicleRepository.findByVehicleType(type);
    }
    
    // Find available vehicles
    @Transactional(readOnly = true)
    public List<Vehicle> getAvailableVehicles() {
        log.info("Fetching available vehicles");
        return vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE);
    }
    
    // Find vehicles by location
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByLocation(String city, String state) {
        log.info("Fetching vehicles in city: {}, state: {}", city, state);
        return vehicleRepository.findByLocationCityAndLocationState(city, state);
    }
    
    // Find vehicles with capacity
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesWithCapacity(BigDecimal minCapacity) {
        log.info("Fetching vehicles with capacity >= {}", minCapacity);
        return vehicleRepository.findByCapacityGreaterThanEqual(minCapacity);
    }
    
    // Find vehicles with documents expiring soon
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesWithExpiringDocuments(LocalDate beforeDate) {
        log.info("Fetching vehicles with documents expiring before: {}", beforeDate);
        return vehicleRepository.findVehiclesWithExpiringDocuments(beforeDate);
    }
    
    // Find verified vehicles
    @Transactional(readOnly = true)
    public List<Vehicle> getVerifiedVehicles() {
        log.info("Fetching verified vehicles");
        return vehicleRepository.findByIsVerifiedTrue();
    }
    
    // Update vehicle status
    public Vehicle updateVehicleStatus(String vehicleId, Vehicle.VehicleStatus status) {
        log.info("Updating status of vehicle {} to {}", vehicleId, status);
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        
        vehicle.setStatus(status);
        vehicle.setUpdatedAt(LocalDateTime.now());
        
        return vehicleRepository.save(vehicle);
    }
    
    // Update vehicle location
    public Vehicle updateVehicleLocation(String vehicleId, Double latitude, Double longitude, String address) {
        log.info("Updating location of vehicle {}", vehicleId);
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        
        vehicle.setCurrentLatitude(latitude);
        vehicle.setCurrentLongitude(longitude);
        vehicle.setCurrentAddress(address);
        vehicle.setLastLocationUpdate(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());
        
        return vehicleRepository.save(vehicle);
    }
    
    // Update next service date
    public Vehicle updateNextServiceDate(String vehicleId, LocalDate nextServiceDate) {
        log.info("Updating next service date for vehicle {}", vehicleId);
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        
        vehicle.setNextServiceDate(nextServiceDate);
        vehicle.setUpdatedAt(LocalDateTime.now());
        
        return vehicleRepository.save(vehicle);
    }
    
    // Update active status
    public Vehicle updateActiveStatus(String vehicleId, boolean isActive, String notes) {
        log.info("Updating active status of vehicle {} to {}", vehicleId, isActive);
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        
        vehicle.setIsActive(isActive);
        vehicle.setUpdatedAt(LocalDateTime.now());
        
        return vehicleRepository.save(vehicle);
    }
    
    // Check if vehicle is available for booking
    @Transactional(readOnly = true)
    public boolean isVehicleAvailable(String vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Checking availability of vehicle {} from {} to {}", vehicleId, startTime, endTime);
        
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isEmpty()) {
            return false;
        }
        
        Vehicle vehicle = vehicleOpt.get();
        
        // Check if vehicle is active
        if (vehicle.getStatus() != Vehicle.VehicleStatus.AVAILABLE || !vehicle.getIsActive()) {
            return false;
        }
        
        // Check if all required documents are valid
        if (isAnyDocumentExpired(vehicle)) {
            return false;
        }
        
        // Additional availability logic can be added here
        // For example, checking existing bookings
        
        return true;
    }
    
    // Check if any document is expired
    private boolean isAnyDocumentExpired(Vehicle vehicle) {
        LocalDate today = LocalDate.now();
        
        if (vehicle.getRegistration() != null && vehicle.getRegistration().getExpiryDate() != null && 
            vehicle.getRegistration().getExpiryDate().isBefore(today)) {
            return true;
        }
        
        if (vehicle.getInsurance() != null && vehicle.getInsurance().getExpiryDate() != null && 
            vehicle.getInsurance().getExpiryDate().isBefore(today)) {
            return true;
        }
        
        if (vehicle.getPollution() != null && vehicle.getPollution().getExpiryDate() != null && 
            vehicle.getPollution().getExpiryDate().isBefore(today)) {
            return true;
        }
        
        if (vehicle.getFitness() != null && vehicle.getFitness().getExpiryDate() != null && 
            vehicle.getFitness().getExpiryDate().isBefore(today)) {
            return true;
        }
        
        if (vehicle.getPermit() != null && vehicle.getPermit().getExpiryDate() != null && 
            vehicle.getPermit().getExpiryDate().isBefore(today)) {
            return true;
        }
        
        return false;
    }
    
    // Delete vehicle
    public void deleteVehicle(String vehicleId) {
        log.info("Deleting vehicle with ID: {}", vehicleId);
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        
        // Check if vehicle can be deleted (no active bookings)
        if (vehicle.getStatus() == Vehicle.VehicleStatus.IN_TRANSIT) {
            throw new RuntimeException("Cannot delete vehicle with active bookings");
        }
        
        vehicleRepository.delete(vehicle);
    }
    
    // Get vehicle statistics
    @Transactional(readOnly = true)
    public VehicleStatistics getVehicleStatistics(String ownerId) {
        log.info("Generating vehicle statistics for owner: {}", ownerId);
        
        List<Vehicle> vehicles = vehicleRepository.findByOwnerId(ownerId);
        
        VehicleStatistics stats = new VehicleStatistics();
        stats.setTotalVehicles(vehicles.size());
        stats.setAvailableVehicles((int) vehicles.stream().filter(v -> v.getStatus() == Vehicle.VehicleStatus.AVAILABLE).count());
        stats.setInTransitVehicles((int) vehicles.stream().filter(v -> v.getStatus() == Vehicle.VehicleStatus.IN_TRANSIT).count());
        stats.setMaintenanceVehicles((int) vehicles.stream().filter(v -> v.getStatus() == Vehicle.VehicleStatus.MAINTENANCE).count());
        stats.setInactiveVehicles((int) vehicles.stream().filter(v -> v.getStatus() == Vehicle.VehicleStatus.INACTIVE).count());
        stats.setActiveVehicles((int) vehicles.stream().filter(v -> v.getIsActive() != null && v.getIsActive()).count());
        
        return stats;
    }
    
    // Inner class for vehicle statistics
    public static class VehicleStatistics {
        private int totalVehicles;
        private int availableVehicles;
        private int inTransitVehicles;
        private int maintenanceVehicles;
        private int inactiveVehicles;
        private int activeVehicles;
        
        // Getters and setters
        public int getTotalVehicles() { return totalVehicles; }
        public void setTotalVehicles(int totalVehicles) { this.totalVehicles = totalVehicles; }
        
        public int getAvailableVehicles() { return availableVehicles; }
        public void setAvailableVehicles(int availableVehicles) { this.availableVehicles = availableVehicles; }
        
        public int getInTransitVehicles() { return inTransitVehicles; }
        public void setInTransitVehicles(int inTransitVehicles) { this.inTransitVehicles = inTransitVehicles; }
        
        public int getMaintenanceVehicles() { return maintenanceVehicles; }
        public void setMaintenanceVehicles(int maintenanceVehicles) { this.maintenanceVehicles = maintenanceVehicles; }
        
        public int getInactiveVehicles() { return inactiveVehicles; }
        public void setInactiveVehicles(int inactiveVehicles) { this.inactiveVehicles = inactiveVehicles; }
        
        public int getActiveVehicles() { return activeVehicles; }
        public void setActiveVehicles(int activeVehicles) { this.activeVehicles = activeVehicles; }
    }
}
