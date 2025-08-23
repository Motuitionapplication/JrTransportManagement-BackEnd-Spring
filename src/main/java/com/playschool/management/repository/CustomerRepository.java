package com.playschool.management.repository;

import com.playschool.management.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import java.util.Optional;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    
    // Find by authentication credentials
    Optional<Customer> findByUserId(String userId);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    
    // Find by user credentials for login
    Optional<Customer> findByUserIdOrEmailOrPhoneNumber(String userId, String email, String phoneNumber);
    
    // Find by verification status
    List<Customer> findByVerificationStatus(Customer.VerificationStatus verificationStatus);
    
    // Find by account status
    List<Customer> findByAccountStatus(Customer.AccountStatus accountStatus);
    
    // Find customers with identity verification
    List<Customer> findByIdentityProofIsVerifiedTrue();
    
    // Find customers with bank verification
    @Query("SELECT c FROM Customer c WHERE c.bankDetails.isVerified = true")
    List<Customer> findByBankDetailsIsVerifiedTrue();
    
    // Find customers by city
    @Query("SELECT c FROM Customer c WHERE c.address.city = :city")
    List<Customer> findByCity(@Param("city") String city);
    
    // Find customers by state
    @Query("SELECT c FROM Customer c WHERE c.address.state = :state")
    List<Customer> findByState(@Param("state") String state);
    
    // Find customers with wallet balance above threshold
    List<Customer> findByWalletBalanceGreaterThan(BigDecimal threshold);
    
    // Find customers with reserved amount
    List<Customer> findByReservedAmountGreaterThan(BigDecimal amount);
    
    // Find customers by rating range
    List<Customer> findByAverageRatingGreaterThanEqual(BigDecimal minRating);
    
    // Find customers by identity proof number
    @Query("SELECT c FROM Customer c WHERE c.identityProof.number = :identityNumber")
    Optional<Customer> findByIdentityProofNumber(@Param("identityNumber") String identityNumber);
    
    // Find customers by bank account number
    @Query("SELECT c FROM Customer c WHERE c.bankDetails.accountNumber = :accountNumber")
    Optional<Customer> findByBankAccountNumber(@Param("accountNumber") String accountNumber);
    
    // Find customers by UPI ID
    @Query("SELECT c FROM Customer c WHERE c.bankDetails.upiId = :upiId")
    Optional<Customer> findByUpiId(@Param("upiId") String upiId);
    
    // Find customers with bookings
    @Query("SELECT c FROM Customer c WHERE SIZE(c.bookingHistory) > 0")
    List<Customer> findCustomersWithBookings();
    
    // Find customers with complaints
    @Query("SELECT c FROM Customer c WHERE SIZE(c.complaints) > 0")
    List<Customer> findCustomersWithComplaints();
    
    // Find customers with support tickets
    @Query("SELECT c FROM Customer c WHERE SIZE(c.supportTickets) > 0")
    List<Customer> findCustomersWithSupportTickets();
    
    // Find customers by preferred vehicle type
    List<Customer> findByPreferredVehicleType(String preferredVehicleType);
    
    // Count customers by verification status
    long countByVerificationStatus(Customer.VerificationStatus verificationStatus);
    
    // Count active customers
    long countByAccountStatus(Customer.AccountStatus accountStatus);
    
    // Find recently registered customers
    @Query("SELECT c FROM Customer c WHERE c.createdAt >= :fromDate ORDER BY c.createdAt DESC")
    List<Customer> findRecentlyRegistered(@Param("fromDate") java.time.LocalDateTime fromDate);
    
    // Find customers with notifications enabled
    List<Customer> findByEmailNotificationsTrue();
    List<Customer> findBySmsNotificationsTrue();
    List<Customer> findByPushNotificationsTrue();
    List<Customer> findByWhatsappNotificationsTrue();
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if phone number exists
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Check if user ID exists
    boolean existsByUserId(String userId);
    
    // Check if identity proof number exists
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.identityProof.number = :identityNumber")
    boolean existsByIdentityProofNumber(@Param("identityNumber") String identityNumber);
    
    // Check if bank account number exists
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.bankDetails.accountNumber = :accountNumber")
    boolean existsByBankAccountNumber(@Param("accountNumber") String accountNumber);

}