package com.playschool.management.controller;

import com.playschool.management.entity.VehicleOwner;
import com.playschool.management.entity.WalletTransaction;
import com.playschool.management.service.VehicleOwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicle-owners")
@Tag(name = "Vehicle Owner Management", description = "APIs for managing vehicle owners in the transport system")
public class VehicleOwnerController {

    private static final Logger log = LoggerFactory.getLogger(VehicleOwnerController.class);
    private final VehicleOwnerService vehicleOwnerService;
    
    // Explicit constructor
    public VehicleOwnerController(VehicleOwnerService vehicleOwnerService) {
        this.vehicleOwnerService = vehicleOwnerService;
    }

    @Operation(summary = "Create or update a vehicle owner", description = "Creates a new vehicle owner or updates an existing one")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle owner saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid owner data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<VehicleOwner> saveVehicleOwner(@Valid @RequestBody VehicleOwner owner) {
        
        log.info("Received request to save vehicle owner: {}", owner.getUserId());
        
        try {
            VehicleOwner savedOwner = vehicleOwnerService.saveVehicleOwner(owner);
            return ResponseEntity.ok(savedOwner);
        } catch (Exception e) {
            log.error("Error saving vehicle owner: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get vehicle owner by ID", description = "Retrieves a vehicle owner by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle owner found"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @GetMapping("/{ownerId}")
    public ResponseEntity<VehicleOwner> getOwnerById(
            @Parameter(description = "Owner ID") @PathVariable String ownerId) {
        
        log.info("Received request to get vehicle owner by ID: {}", ownerId);
        
        Optional<VehicleOwner> owner = vehicleOwnerService.findOwnerById(ownerId);
        return owner.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all vehicle owners", description = "Retrieves all vehicle owners in the system")
    @ApiResponse(responseCode = "200", description = "List of vehicle owners retrieved successfully")
    @GetMapping
    public ResponseEntity<List<VehicleOwner>> getAllOwners() {
        
        log.info("Received request to get all vehicle owners");
        
        List<VehicleOwner> owners = vehicleOwnerService.getAllOwners();
        return ResponseEntity.ok(owners);
    }

    @Operation(summary = "Authenticate vehicle owner", description = "Authenticates a vehicle owner with credentials")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<VehicleOwner> authenticateOwner(
            @Parameter(description = "User ID, Email, or Phone Number") @RequestParam String credentials,
            @Parameter(description = "Password") @RequestParam String password) {
        
        log.info("Received authentication request for: {}", credentials);
        
        Optional<VehicleOwner> owner = vehicleOwnerService.authenticateOwner(credentials, password);
        return owner.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Get owners by verification status", description = "Retrieves vehicle owners by their verification status")
    @ApiResponse(responseCode = "200", description = "List of vehicle owners retrieved successfully")
    @GetMapping("/verification-status/{status}")
    public ResponseEntity<List<VehicleOwner>> getOwnersByVerificationStatus(
            @Parameter(description = "Verification status") @PathVariable VehicleOwner.VerificationStatus status) {
        
        log.info("Received request to get owners with verification status: {}", status);
        
        List<VehicleOwner> owners = vehicleOwnerService.getOwnersByVerificationStatus(status);
        return ResponseEntity.ok(owners);
    }

    @Operation(summary = "Get owners by account status", description = "Retrieves vehicle owners by their account status")
    @ApiResponse(responseCode = "200", description = "List of vehicle owners retrieved successfully")
    @GetMapping("/account-status/{status}")
    public ResponseEntity<List<VehicleOwner>> getOwnersByAccountStatus(
            @Parameter(description = "Account status") @PathVariable VehicleOwner.AccountStatus status) {
        
        log.info("Received request to get owners with account status: {}", status);
        
        List<VehicleOwner> owners = vehicleOwnerService.getOwnersByAccountStatus(status);
        return ResponseEntity.ok(owners);
    }

    @Operation(summary = "Get verified owners", description = "Retrieves all verified vehicle owners")
    @ApiResponse(responseCode = "200", description = "List of verified vehicle owners retrieved successfully")
    @GetMapping("/verified")
    public ResponseEntity<List<VehicleOwner>> getVerifiedOwners() {
        
        log.info("Received request to get verified owners");
        
        List<VehicleOwner> owners = vehicleOwnerService.getVerifiedOwners();
        return ResponseEntity.ok(owners);
    }

    @Operation(summary = "Get owners by city", description = "Retrieves vehicle owners in a specific city")
    @ApiResponse(responseCode = "200", description = "List of vehicle owners retrieved successfully")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<VehicleOwner>> getOwnersByCity(
            @Parameter(description = "City name") @PathVariable String city) {
        
        log.info("Received request to get owners in city: {}", city);
        
        List<VehicleOwner> owners = vehicleOwnerService.getOwnersByCity(city);
        return ResponseEntity.ok(owners);
    }

    @Operation(summary = "Get owners with wallet balance", description = "Retrieves owners with wallet balance above threshold")
    @ApiResponse(responseCode = "200", description = "List of vehicle owners retrieved successfully")
    @GetMapping("/wallet-balance")
    public ResponseEntity<List<VehicleOwner>> getOwnersWithWalletBalance(
            @Parameter(description = "Minimum wallet balance") @RequestParam BigDecimal threshold) {
        
        log.info("Received request to get owners with wallet balance > {}", threshold);
        
        List<VehicleOwner> owners = vehicleOwnerService.getOwnersWithWalletBalance(threshold);
        return ResponseEntity.ok(owners);
    }

    @Operation(summary = "Update verification status", description = "Updates the verification status of a vehicle owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @PutMapping("/{ownerId}/verification-status")
    public ResponseEntity<VehicleOwner> updateVerificationStatus(
            @Parameter(description = "Owner ID") @PathVariable String ownerId,
            @Parameter(description = "Verification status") @RequestParam VehicleOwner.VerificationStatus status,
            @Parameter(description = "Verification notes") @RequestParam(required = false) String notes) {
        
        log.info("Received request to update verification status of owner {} to {}", ownerId, status);
        
        try {
            VehicleOwner updatedOwner = vehicleOwnerService.updateVerificationStatus(ownerId, status, notes);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            log.error("Error updating verification status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update account status", description = "Updates the account status of a vehicle owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @PutMapping("/{ownerId}/account-status")
    public ResponseEntity<VehicleOwner> updateAccountStatus(
            @Parameter(description = "Owner ID") @PathVariable String ownerId,
            @Parameter(description = "Account status") @RequestParam VehicleOwner.AccountStatus status) {
        
        log.info("Received request to update account status of owner {} to {}", ownerId, status);
        
        try {
            VehicleOwner updatedOwner = vehicleOwnerService.updateAccountStatus(ownerId, status);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            log.error("Error updating account status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add money to wallet", description = "Adds money to a vehicle owner's wallet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Money added to wallet successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @PostMapping("/{ownerId}/wallet/add")
    public ResponseEntity<VehicleOwner> addToWallet(
            @Parameter(description = "Owner ID") @PathVariable String ownerId,
            @Parameter(description = "Amount to add") @RequestParam BigDecimal amount,
            @Parameter(description = "Transaction description") @RequestParam String description,
            @Parameter(description = "Transaction type") @RequestParam String transactionType) {
        
        log.info("Received request to add {} to wallet of owner {}", amount, ownerId);
        
        try {
            VehicleOwner updatedOwner = vehicleOwnerService.addToWallet(ownerId, amount, description, transactionType);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            log.error("Error adding to wallet: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deduct money from wallet", description = "Deducts money from a vehicle owner's wallet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Money deducted from wallet successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found"),
        @ApiResponse(responseCode = "400", description = "Insufficient wallet balance")
    })
    @PostMapping("/{ownerId}/wallet/deduct")
    public ResponseEntity<VehicleOwner> deductFromWallet(
            @Parameter(description = "Owner ID") @PathVariable String ownerId,
            @Parameter(description = "Amount to deduct") @RequestParam BigDecimal amount,
            @Parameter(description = "Transaction description") @RequestParam String description,
            @Parameter(description = "Transaction type") @RequestParam String transactionType) {
        
        log.info("Received request to deduct {} from wallet of owner {}", amount, ownerId);
        
        try {
            VehicleOwner updatedOwner = vehicleOwnerService.deductFromWallet(ownerId, amount, description, transactionType);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            log.error("Error deducting from wallet: {}", e.getMessage());
            if (e.getMessage().contains("Insufficient")) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @Operation(summary = "Update identity verification", description = "Updates the identity verification status of a vehicle owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Identity verification updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @PutMapping("/{ownerId}/identity-verification")
    public ResponseEntity<VehicleOwner> updateIdentityVerification(
            @Parameter(description = "Owner ID") @PathVariable String ownerId,
            @Parameter(description = "Verification status") @RequestParam Boolean isVerified,
            @Parameter(description = "Verification notes") @RequestParam(required = false) String notes) {
        
        log.info("Received request to update identity verification of owner {} to {}", ownerId, isVerified);
        
        try {
            VehicleOwner updatedOwner = vehicleOwnerService.updateIdentityVerification(ownerId, isVerified, notes);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            log.error("Error updating identity verification: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update business verification", description = "Updates the business verification status of a vehicle owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Business verification updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @PutMapping("/{ownerId}/business-verification")
    public ResponseEntity<VehicleOwner> updateBusinessVerification(
            @Parameter(description = "Owner ID") @PathVariable String ownerId,
            @Parameter(description = "Verification status") @RequestParam Boolean isVerified,
            @Parameter(description = "Verification notes") @RequestParam(required = false) String notes) {
        
        log.info("Received request to update business verification of owner {} to {}", ownerId, isVerified);
        
        try {
            VehicleOwner updatedOwner = vehicleOwnerService.updateBusinessVerification(ownerId, isVerified, notes);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            log.error("Error updating business verification: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Change password", description = "Changes the password of a vehicle owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found"),
        @ApiResponse(responseCode = "400", description = "Current password is incorrect")
    })
    @PutMapping("/{ownerId}/change-password")
    public ResponseEntity<VehicleOwner> changePassword(
            @Parameter(description = "Owner ID") @PathVariable String ownerId,
            @Parameter(description = "Current password") @RequestParam String oldPassword,
            @Parameter(description = "New password") @RequestParam String newPassword) {
        
        log.info("Received request to change password for owner {}", ownerId);
        
        try {
            VehicleOwner updatedOwner = vehicleOwnerService.changePassword(ownerId, oldPassword, newPassword);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            log.error("Error changing password: {}", e.getMessage());
            if (e.getMessage().contains("incorrect")) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @Operation(summary = "Get wallet transactions", description = "Retrieves wallet transactions for a vehicle owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wallet transactions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @GetMapping("/{ownerId}/wallet/transactions")
    public ResponseEntity<List<WalletTransaction>> getWalletTransactions(
            @Parameter(description = "Owner ID") @PathVariable String ownerId) {
        
        log.info("Received request to get wallet transactions for owner: {}", ownerId);
        
        try {
            List<WalletTransaction> transactions = vehicleOwnerService.getWalletTransactions(ownerId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            log.error("Error getting wallet transactions: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Check if email exists", description = "Checks if an email address is already registered")
    @ApiResponse(responseCode = "200", description = "Email existence status returned")
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Email address") @RequestParam String email) {
        
        log.info("Received request to check if email exists: {}", email);
        
        boolean exists = vehicleOwnerService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Check if phone number exists", description = "Checks if a phone number is already registered")
    @ApiResponse(responseCode = "200", description = "Phone number existence status returned")
    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhoneNumberExists(
            @Parameter(description = "Phone number") @RequestParam String phoneNumber) {
        
        log.info("Received request to check if phone number exists: {}", phoneNumber);
        
        boolean exists = vehicleOwnerService.phoneNumberExists(phoneNumber);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Check if user ID exists", description = "Checks if a user ID is already registered")
    @ApiResponse(responseCode = "200", description = "User ID existence status returned")
    @GetMapping("/check-userid")
    public ResponseEntity<Boolean> checkUserIdExists(
            @Parameter(description = "User ID") @RequestParam String userId) {
        
        log.info("Received request to check if user ID exists: {}", userId);
        
        boolean exists = vehicleOwnerService.userIdExists(userId);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Delete vehicle owner", description = "Deletes a vehicle owner from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vehicle owner deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle owner not found")
    })
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(
            @Parameter(description = "Owner ID") @PathVariable String ownerId) {
        
        log.info("Received request to delete vehicle owner {}", ownerId);
        
        try {
            vehicleOwnerService.deleteOwner(ownerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting vehicle owner: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get dashboard statistics", description = "Retrieves dashboard statistics for a vehicle owner")
    @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully")
    @GetMapping("/{ownerId}/dashboard-stats")
    public ResponseEntity<VehicleOwnerService.OwnerDashboardStats> getDashboardStats(
            @Parameter(description = "Owner ID") @PathVariable String ownerId) {
        
        log.info("Received request to get dashboard statistics for owner: {}", ownerId);
        
        try {
            VehicleOwnerService.OwnerDashboardStats stats = vehicleOwnerService.getDashboardStats(ownerId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.error("Error getting dashboard statistics: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
