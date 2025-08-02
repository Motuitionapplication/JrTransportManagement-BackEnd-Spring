package com.playschool.management.service;

import com.playschool.management.entity.VehicleOwner;
import com.playschool.management.entity.WalletTransaction;
import com.playschool.management.repository.VehicleOwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleOwnerService {
    
    private static final Logger log = LoggerFactory.getLogger(VehicleOwnerService.class);
    private final VehicleOwnerRepository vehicleOwnerRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Explicit constructor
    public VehicleOwnerService(VehicleOwnerRepository vehicleOwnerRepository, PasswordEncoder passwordEncoder) {
        this.vehicleOwnerRepository = vehicleOwnerRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Create or update vehicle owner
    public VehicleOwner saveVehicleOwner(VehicleOwner owner) {
        log.info("Saving vehicle owner with ID: {}", owner.getOwnerId());
        
        // Encode password if it's a new owner or password is being updated
        if (owner.getCreatedAt() == null) {
            owner.setCreatedAt(LocalDateTime.now());
            if (owner.getPassword() != null) {
                owner.setPassword(passwordEncoder.encode(owner.getPassword()));
            }
        }
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Find owner by ID
    @Transactional(readOnly = true)
    public Optional<VehicleOwner> findOwnerById(String ownerId) {
        log.info("Finding owner by ID: {}", ownerId);
        return vehicleOwnerRepository.findById(ownerId);
    }
    
    // Find owner by user credentials (login)
    @Transactional(readOnly = true)
    public Optional<VehicleOwner> findOwnerByCredentials(String userIdOrEmailOrPhone) {
        log.info("Finding owner by credentials: {}", userIdOrEmailOrPhone);
        return vehicleOwnerRepository.findByUserIdOrEmailOrPhoneNumber(
            userIdOrEmailOrPhone, userIdOrEmailOrPhone, userIdOrEmailOrPhone);
    }
    
    // Authenticate owner
    @Transactional(readOnly = true)
    public Optional<VehicleOwner> authenticateOwner(String userIdOrEmailOrPhone, String password) {
        log.info("Authenticating owner: {}", userIdOrEmailOrPhone);
        
        Optional<VehicleOwner> ownerOpt = findOwnerByCredentials(userIdOrEmailOrPhone);
        
        if (ownerOpt.isPresent() && passwordEncoder.matches(password, ownerOpt.get().getPassword())) {
            return ownerOpt;
        }
        
        return Optional.empty();
    }
    
    // Get all owners
    @Transactional(readOnly = true)
    public List<VehicleOwner> getAllOwners() {
        log.info("Fetching all vehicle owners");
        return vehicleOwnerRepository.findAll();
    }
    
    // Find owners by verification status
    @Transactional(readOnly = true)
    public List<VehicleOwner> getOwnersByVerificationStatus(VehicleOwner.VerificationStatus status) {
        log.info("Fetching owners with verification status: {}", status);
        return vehicleOwnerRepository.findByVerificationStatus(status);
    }
    
    // Find owners by account status
    @Transactional(readOnly = true)
    public List<VehicleOwner> getOwnersByAccountStatus(VehicleOwner.AccountStatus status) {
        log.info("Fetching owners with account status: {}", status);
        return vehicleOwnerRepository.findByAccountStatus(status);
    }
    
    // Find verified owners
    @Transactional(readOnly = true)
    public List<VehicleOwner> getVerifiedOwners() {
        log.info("Fetching verified owners");
        return vehicleOwnerRepository.findByIdentityProofIsVerifiedTrue();
    }
    
    // Find owners by city
    @Transactional(readOnly = true)
    public List<VehicleOwner> getOwnersByCity(String city) {
        log.info("Fetching owners in city: {}", city);
        return vehicleOwnerRepository.findByCity(city);
    }
    
    // Find owners with wallet balance above threshold
    @Transactional(readOnly = true)
    public List<VehicleOwner> getOwnersWithWalletBalance(BigDecimal threshold) {
        log.info("Fetching owners with wallet balance > {}", threshold);
        return vehicleOwnerRepository.findByWalletBalanceGreaterThan(threshold);
    }
    
    // Update verification status
    public VehicleOwner updateVerificationStatus(String ownerId, VehicleOwner.VerificationStatus status, String notes) {
        log.info("Updating verification status of owner {} to {}", ownerId, status);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        owner.setVerificationStatus(status);
        owner.setVerificationNotes(notes);
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Update account status
    public VehicleOwner updateOwner(String ownerId, VehicleOwner updatedOwner) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found"));
        owner.setFirstName(updatedOwner.getFirstName());
        owner.setLastName(updatedOwner.getLastName());
        owner.setEmail(updatedOwner.getEmail());
        owner.setPhoneNumber(updatedOwner.getPhoneNumber());
        owner.setAccountStatus(updatedOwner.getAccountStatus());
        return vehicleOwnerRepository.save(owner);
    }
    
    // Add money to wallet
    public VehicleOwner addToWallet(String ownerId, BigDecimal amount, String description, String transactionType) {
        log.info("Adding {} to wallet of owner {}", amount, ownerId);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        // Update wallet balance
        BigDecimal currentBalance = owner.getWalletBalance() != null ? owner.getWalletBalance() : BigDecimal.ZERO;
        owner.setWalletBalance(currentBalance.add(amount));
        
        // Create wallet transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionId("TXN_" + System.currentTimeMillis());
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setBalanceAfter(owner.getWalletBalance());
        
        owner.getWalletTransactions().add(transaction);
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Deduct money from wallet
    public VehicleOwner deductFromWallet(String ownerId, BigDecimal amount, String description, String transactionType) {
        log.info("Deducting {} from wallet of owner {}", amount, ownerId);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        BigDecimal currentBalance = owner.getWalletBalance() != null ? owner.getWalletBalance() : BigDecimal.ZERO;
        
        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }
        
        // Update wallet balance
        owner.setWalletBalance(currentBalance.subtract(amount));
        
        // Create wallet transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionId("TXN_" + System.currentTimeMillis());
        transaction.setAmount(amount.negate()); // Negative for deduction
        transaction.setTransactionType(transactionType);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setBalanceAfter(owner.getWalletBalance());
        
        owner.getWalletTransactions().add(transaction);
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Update identity verification
    public VehicleOwner updateIdentityVerification(String ownerId, boolean isVerified, String verificationNotes) {
        log.info("Updating identity verification of owner {} to {}", ownerId, isVerified);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        VehicleOwner.IdentityProof identityProof = owner.getIdentityProof();
        if (identityProof != null) {
            identityProof.setIsVerified(isVerified);
            identityProof.setVerificationDate(LocalDateTime.now());
            identityProof.setVerificationNotes(verificationNotes);
        }
        
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Update business verification
    public VehicleOwner updateBusinessVerification(String ownerId, boolean isVerified, String verificationNotes) {
        log.info("Updating business verification of owner {} to {}", ownerId, isVerified);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        VehicleOwner.BusinessDetails businessDetails = owner.getBusinessDetails();
        if (businessDetails != null) {
            businessDetails.setIsVerified(isVerified);
            businessDetails.setVerificationDate(LocalDateTime.now());
            businessDetails.setVerificationNotes(verificationNotes);
        }
        
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Update owner rating
    public VehicleOwner updateOwnerRating(String ownerId, BigDecimal newRating) {
        log.info("Updating rating of owner {} to {}", ownerId, newRating);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        // Calculate new average rating
        BigDecimal currentRating = owner.getAverageRating() != null ? owner.getAverageRating() : BigDecimal.ZERO;
        Integer totalRatings = owner.getTotalRatings() != null ? owner.getTotalRatings() : 0;
        
        BigDecimal totalScore = currentRating.multiply(BigDecimal.valueOf(totalRatings)).add(newRating);
        totalRatings++;
        BigDecimal newAverageRating = totalScore.divide(BigDecimal.valueOf(totalRatings), 2, RoundingMode.HALF_UP);
        
        owner.setAverageRating(newAverageRating);
        owner.setTotalRatings(totalRatings);
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Change password
    public VehicleOwner changePassword(String ownerId, String oldPassword, String newPassword) {
        log.info("Changing password for owner {}", ownerId);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        if (!passwordEncoder.matches(oldPassword, owner.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        owner.setPassword(passwordEncoder.encode(newPassword));
        owner.setUpdatedAt(LocalDateTime.now());
        
        return vehicleOwnerRepository.save(owner);
    }
    
    // Get wallet transactions
    @Transactional(readOnly = true)
    public List<WalletTransaction> getWalletTransactions(String ownerId) {
        log.info("Fetching wallet transactions for owner: {}", ownerId);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        return owner.getWalletTransactions();
    }
    
    // Check if email exists
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return vehicleOwnerRepository.existsByEmail(email);
    }
    
    // Check if phone number exists
    @Transactional(readOnly = true)
    public boolean phoneNumberExists(String phoneNumber) {
        return vehicleOwnerRepository.existsByPhoneNumber(phoneNumber);
    }
    
    // Check if user ID exists
    @Transactional(readOnly = true)
    public boolean userIdExists(String userId) {
        return vehicleOwnerRepository.existsByUserId(userId);
    }
    
    // Delete owner
    public void deleteOwner(String ownerId) {
        log.info("Deleting owner with ID: {}", ownerId);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        // Check if owner can be deleted (no active vehicles or bookings)
        // Additional validation logic can be added here
        
        vehicleOwnerRepository.delete(owner);
    }
    
    // Get owner dashboard statistics
    @Transactional(readOnly = true)
    public OwnerDashboardStats getDashboardStats(String ownerId) {
        log.info("Generating dashboard statistics for owner: {}", ownerId);
        
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        
        OwnerDashboardStats stats = new OwnerDashboardStats();
        stats.setWalletBalance(owner.getWalletBalance());
        stats.setTotalRatings(owner.getTotalRatings());
        stats.setAverageRating(owner.getAverageRating());
        stats.setVerificationStatus(owner.getVerificationStatus());
        stats.setAccountStatus(owner.getAccountStatus());
        
        // Additional statistics can be calculated here
        // For example, total vehicles, active bookings, earnings, etc.
        
        return stats;
    }
    
    // Inner class for dashboard statistics
    public static class OwnerDashboardStats {
        private BigDecimal walletBalance;
        private Integer totalRatings;
        private BigDecimal averageRating;
        private VehicleOwner.VerificationStatus verificationStatus;
        private VehicleOwner.AccountStatus accountStatus;
        
        // Getters and setters
        public BigDecimal getWalletBalance() { return walletBalance; }
        public void setWalletBalance(BigDecimal walletBalance) { this.walletBalance = walletBalance; }
        
        public Integer getTotalRatings() { return totalRatings; }
        public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }
        
        public BigDecimal getAverageRating() { return averageRating; }
        public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
        
        public VehicleOwner.VerificationStatus getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(VehicleOwner.VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
        
        public VehicleOwner.AccountStatus getAccountStatus() { return accountStatus; }
        public void setAccountStatus(VehicleOwner.AccountStatus accountStatus) { this.accountStatus = accountStatus; }
    }
}
