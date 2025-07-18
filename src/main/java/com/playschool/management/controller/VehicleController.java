package com.playschool.management.controller;

import com.playschool.management.entity.Vehicle;
import com.playschool.management.service.VehicleService;
import com.playschool.management.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicle Management", description = "APIs for managing vehicles in the transport system")
@CrossOrigin(origins = "*")
public class VehicleController {

    private static final Logger log = LoggerFactory.getLogger(VehicleController.class);
    private final VehicleService vehicleService;
    
    // Explicit constructor
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Create or update a vehicle", description = "Creates a new vehicle or updates an existing one")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApplicationConstants.HttpStatus.OK, description = ApplicationConstants.Messages.VEHICLE_SAVED_SUCCESSFULLY),
        @ApiResponse(responseCode = ApplicationConstants.HttpStatus.BAD_REQUEST, description = ApplicationConstants.Messages.INVALID_VEHICLE_DATA),
        @ApiResponse(responseCode = ApplicationConstants.HttpStatus.INTERNAL_SERVER_ERROR, description = ApplicationConstants.Messages.INTERNAL_SERVER_ERROR)
    })
    @PostMapping
    public ResponseEntity<Vehicle> saveVehicle(
            @Valid @RequestBody Vehicle vehicle,
            @Parameter(description = "Vehicle data to save") @RequestParam(required = false) String operation) {
        
        log.info("Received request to save vehicle: {}", vehicle.getVehicleNumber());
        
        try {
            Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
            return ResponseEntity.ok(savedVehicle);
        } catch (Exception e) {
            log.error("Error saving vehicle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get vehicle by ID", description = "Retrieves a vehicle by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApplicationConstants.HttpStatus.OK, description = "Vehicle found"),
        @ApiResponse(responseCode = ApplicationConstants.HttpStatus.NOT_FOUND, description = ApplicationConstants.Messages.VEHICLE_NOT_FOUND)
    })
    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(
            @Parameter(description = "Vehicle ID") @PathVariable String vehicleId) {
        
        log.info("Received request to get vehicle by ID: {}", vehicleId);
        
        Optional<Vehicle> vehicle = vehicleService.findVehicleById(vehicleId);
        return vehicle.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all vehicles", description = "Retrieves all vehicles in the system")
    @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        
        log.info("Received request to get all vehicles");
        
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Get vehicles by owner", description = "Retrieves all vehicles owned by a specific owner")
    @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByOwner(
            @Parameter(description = "Owner ID") @PathVariable String ownerId) {
        
        log.info("Received request to get vehicles for owner: {}", ownerId);
        
        List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(ownerId);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Get vehicles by type", description = "Retrieves all vehicles of a specific type")
    @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully")
    @GetMapping("/type/{vehicleType}")
    public ResponseEntity<List<Vehicle>> getVehiclesByType(
            @Parameter(description = "Vehicle type") @PathVariable String vehicleType) {
        
        log.info("Received request to get vehicles of type: {}", vehicleType);
        
        List<Vehicle> vehicles = vehicleService.getVehiclesByType(vehicleType);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Get available vehicles", description = "Retrieves all available vehicles")
    @ApiResponse(responseCode = "200", description = "List of available vehicles retrieved successfully")
    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        
        log.info("Received request to get available vehicles");
        
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Get vehicles by location", description = "Retrieves vehicles in a specific city and state")
    @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully")
    @GetMapping("/location")
    public ResponseEntity<List<Vehicle>> getVehiclesByLocation(
            @Parameter(description = "City name") @RequestParam String city,
            @Parameter(description = "State name") @RequestParam String state) {
        
        log.info("Received request to get vehicles in city: {}, state: {}", city, state);
        
        List<Vehicle> vehicles = vehicleService.getVehiclesByLocation(city, state);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Get vehicles with minimum capacity", description = "Retrieves vehicles with capacity greater than or equal to specified value")
    @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully")
    @GetMapping("/capacity")
    public ResponseEntity<List<Vehicle>> getVehiclesWithCapacity(
            @Parameter(description = "Minimum capacity") @RequestParam BigDecimal minCapacity) {
        
        log.info("Received request to get vehicles with capacity >= {}", minCapacity);
        
        List<Vehicle> vehicles = vehicleService.getVehiclesWithCapacity(minCapacity);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(summary = "Get vehicles with expiring documents", description = "Retrieves vehicles with documents expiring before a specific date")
    @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully")
    @GetMapping("/expiring-documents")
    public ResponseEntity<List<Vehicle>> getVehiclesWithExpiringDocuments(
            @Parameter(description = "Date before which documents expire (YYYY-MM-DD)") @RequestParam String beforeDate) {
        
        log.info("Received request to get vehicles with documents expiring before: {}", beforeDate);
        
        try {
            LocalDate date = LocalDate.parse(beforeDate);
            List<Vehicle> vehicles = vehicleService.getVehiclesWithExpiringDocuments(date);
            return ResponseEntity.ok(vehicles);
        } catch (Exception e) {
            log.error("Error parsing date: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update vehicle status", description = "Updates the status of a vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    @PutMapping("/{vehicleId}/status")
    public ResponseEntity<Vehicle> updateVehicleStatus(
            @Parameter(description = "Vehicle ID") @PathVariable String vehicleId,
            @Parameter(description = "New status") @RequestParam Vehicle.VehicleStatus status) {
        
        log.info("Received request to update status of vehicle {} to {}", vehicleId, status);
        
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicleStatus(vehicleId, status);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            log.error("Error updating vehicle status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update vehicle location", description = "Updates the current location of a vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle location updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PutMapping("/{vehicleId}/location")
    public ResponseEntity<Vehicle> updateVehicleLocation(
            @Parameter(description = "Vehicle ID") @PathVariable String vehicleId,
            @Parameter(description = "Latitude") @RequestParam Double latitude,
            @Parameter(description = "Longitude") @RequestParam Double longitude,
            @Parameter(description = "Address") @RequestParam String address) {
        
        log.info("Received request to update location of vehicle {}", vehicleId);
        
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicleLocation(vehicleId, latitude, longitude, address);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            log.error("Error updating vehicle location: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update next service date", description = "Updates the next service date for a vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Next service date updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    @PutMapping("/{vehicleId}/service-date")
    public ResponseEntity<Vehicle> updateNextServiceDate(
            @Parameter(description = "Vehicle ID") @PathVariable String vehicleId,
            @Parameter(description = "Next service date (YYYY-MM-DD)") @RequestParam String serviceDate) {
        
        log.info("Received request to update next service date for vehicle {}", vehicleId);
        
        try {
            LocalDate date = LocalDate.parse(serviceDate);
            Vehicle updatedVehicle = vehicleService.updateNextServiceDate(vehicleId, date);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            log.error("Error updating next service date: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error parsing date: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update vehicle active status", description = "Updates the active status of a vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle active status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PutMapping("/{vehicleId}/active-status")
    public ResponseEntity<Vehicle> updateActiveStatus(
            @Parameter(description = "Vehicle ID") @PathVariable String vehicleId,
            @Parameter(description = "Active status") @RequestParam Boolean isActive,
            @Parameter(description = "Notes") @RequestParam(required = false) String notes) {
        
        log.info("Received request to update active status of vehicle {} to {}", vehicleId, isActive);
        
        try {
            Vehicle updatedVehicle = vehicleService.updateActiveStatus(vehicleId, isActive, notes);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            log.error("Error updating vehicle active status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Check vehicle availability", description = "Checks if a vehicle is available for booking in a specific time period")
    @ApiResponse(responseCode = "200", description = "Availability status returned")
    @GetMapping("/{vehicleId}/availability")
    public ResponseEntity<Boolean> checkVehicleAvailability(
            @Parameter(description = "Vehicle ID") @PathVariable String vehicleId,
            @Parameter(description = "Start time (ISO format)") @RequestParam String startTime,
            @Parameter(description = "End time (ISO format)") @RequestParam String endTime) {
        
        log.info("Received request to check availability of vehicle {}", vehicleId);
        
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            boolean isAvailable = vehicleService.isVehicleAvailable(vehicleId, start, end);
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            log.error("Error checking vehicle availability: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete vehicle", description = "Deletes a vehicle from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete vehicle with active bookings")
    })
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "Vehicle ID") @PathVariable String vehicleId) {
        
        log.info("Received request to delete vehicle {}", vehicleId);
        
        try {
            vehicleService.deleteVehicle(vehicleId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting vehicle: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @Operation(summary = "Get vehicle statistics", description = "Retrieves statistics for vehicles owned by a specific owner")
    @ApiResponse(responseCode = "200", description = "Vehicle statistics retrieved successfully")
    @GetMapping("/statistics/{ownerId}")
    public ResponseEntity<VehicleService.VehicleStatistics> getVehicleStatistics(
            @Parameter(description = "Owner ID") @PathVariable String ownerId) {
        
        log.info("Received request to get vehicle statistics for owner: {}", ownerId);
        
        VehicleService.VehicleStatistics stats = vehicleService.getVehicleStatistics(ownerId);
        return ResponseEntity.ok(stats);
    }
}
