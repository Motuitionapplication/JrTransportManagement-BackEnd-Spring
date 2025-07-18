package com.playschool.management.repository;

import com.playschool.management.entity.VehicleOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, String> {
    
    // Find by authentication credentials
    Optional<VehicleOwner> findByUserId(String userId);
    Optional<VehicleOwner> findByEmail(String email);
    Optional<VehicleOwner> findByPhoneNumber(String phoneNumber);
    
    // Find by user credentials for login
    Optional<VehicleOwner> findByUserIdOrEmailOrPhoneNumber(String userId, String email, String phoneNumber);
    
    // Find by verification status
    List<VehicleOwner> findByVerificationStatus(VehicleOwner.VerificationStatus verificationStatus);
    
    // Find by account status
    List<VehicleOwner> findByAccountStatus(VehicleOwner.AccountStatus accountStatus);
    
    // Find by business type
    List<VehicleOwner> findByBusinessDetailsBusinessType(VehicleOwner.BusinessType businessType);
    
    // Find owners with identity verification
    List<VehicleOwner> findByIdentityProofIsVerifiedTrue();
    
    // Find owners with bank verification
    List<VehicleOwner> findByBankDetailsIsVerifiedTrue();
    
    // Find owners by city
    @Query("SELECT o FROM VehicleOwner o WHERE o.address.city = :city")
    List<VehicleOwner> findByCity(@Param("city") String city);
    
    // Find owners by state
    @Query("SELECT o FROM VehicleOwner o WHERE o.address.state = :state")
    List<VehicleOwner> findByState(@Param("state") String state);
    
    // Find owners with wallet balance above threshold
    List<VehicleOwner> findByWalletBalanceGreaterThan(BigDecimal threshold);
    
    // Find owners with reserved amount
    List<VehicleOwner> findByReservedAmountGreaterThan(BigDecimal amount);
    
    // Find owners by PAN number
    Optional<VehicleOwner> findByPanNumber(String panNumber);
    
    // Find owners by GST number
    Optional<VehicleOwner> findByGstNumber(String gstNumber);
    
    // Find owners by bank account number
    @Query("SELECT o FROM VehicleOwner o WHERE o.bankDetails.accountNumber = :accountNumber")
    Optional<VehicleOwner> findByBankAccountNumber(@Param("accountNumber") String accountNumber);
    
    // Find owners by identity proof number
    @Query("SELECT o FROM VehicleOwner o WHERE o.identityProof.number = :identityNumber")
    Optional<VehicleOwner> findByIdentityProofNumber(@Param("identityNumber") String identityNumber);
    
    // Count owners by verification status
    long countByVerificationStatus(VehicleOwner.VerificationStatus verificationStatus);
    
    // Count active owners
    long countByAccountStatus(VehicleOwner.AccountStatus accountStatus);
    
    // Find recently registered owners
    @Query("SELECT o FROM VehicleOwner o WHERE o.createdAt >= :fromDate ORDER BY o.createdAt DESC")
    List<VehicleOwner> findRecentlyRegistered(@Param("fromDate") java.time.LocalDateTime fromDate);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if phone number exists
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Check if user ID exists
    boolean existsByUserId(String userId);
    
    // Check if PAN number exists
    boolean existsByPanNumber(String panNumber);
}
