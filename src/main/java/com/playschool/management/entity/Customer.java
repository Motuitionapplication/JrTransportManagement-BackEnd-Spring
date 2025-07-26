
package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    // Explicit getters for DTO mapping (for Lombok/IDE compatibility)
    public String getAlternatePhone() { return alternatePhone; }
    public VehicleOwner.Address getAddress() { return address; }
    public String getProfilePhoto() { return profilePhoto; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getFatherName() { return fatherName; }
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    // Explicit getters for DTO mapping
    public String getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    // Profile Information

    @Column(nullable = true)
    private String firstName;
    @Column(nullable = true)
    private String lastName;
    @Column(nullable = true)
    private String fatherName;
    
    @Email(message = "Valid email is required")
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true)
    private String phoneNumber;
    
    private String alternatePhone;
    
    // Address
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "address_street")),
        @AttributeOverride(name = "city", column = @Column(name = "address_city")),
        @AttributeOverride(name = "state", column = @Column(name = "address_state")),
        @AttributeOverride(name = "pincode", column = @Column(name = "address_pincode")),
        @AttributeOverride(name = "country", column = @Column(name = "address_country"))
    })
    private VehicleOwner.Address address;
    
    @Column(nullable = true)
    private String profilePhoto; // captured with eye blinking verification
    
    private LocalDate dateOfBirth;
    
    // Authentication
    @Column(unique = true, nullable = false)
    private String userId;
    
    private String password; // This will be hashed
    
    // Identity Proof
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "type", column = @Column(name = "identity_type")),
        @AttributeOverride(name = "number", column = @Column(name = "identity_number")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "identity_document_url")),
        @AttributeOverride(name = "isVerified", column = @Column(name = "identity_verified")),
        @AttributeOverride(name = "verificationDate", column = @Column(name = "identity_verification_date"))
    })
    private VehicleOwner.IdentityProof identityProof;
    
    // Bank Details
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "accountNumber", column = @Column(name = "bank_account_number")),
        @AttributeOverride(name = "accountHolderName", column = @Column(name = "bank_account_holder_name")),
        @AttributeOverride(name = "ifscCode", column = @Column(name = "bank_ifsc_code")),
        @AttributeOverride(name = "bankName", column = @Column(name = "bank_name")),
        @AttributeOverride(name = "branchName", column = @Column(name = "bank_branch_name")),
        @AttributeOverride(name = "branchAddress", column = @Column(name = "bank_branch_address")),
        @AttributeOverride(name = "passbookPhoto", column = @Column(name = "bank_passbook_photo")),
        @AttributeOverride(name = "upiId", column = @Column(name = "bank_upi_id")),
        @AttributeOverride(name = "phonePayNumber", column = @Column(name = "bank_phonepay_number")),
        @AttributeOverride(name = "isVerified", column = @Column(name = "bank_verified")),
        @AttributeOverride(name = "verificationDate", column = @Column(name = "bank_verification_date"))
    })
    private CustomerBankDetails bankDetails;
    
    // Wallet
    @Column(precision = 10, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal reservedAmount = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerWalletTransaction> walletTransactions = new ArrayList<>();
    
    // Booking History
    @ElementCollection
    @CollectionTable(name = "customer_booking_history", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "booking_id")
    private List<String> bookingHistory = new ArrayList<>();
    
    // Preferences
    private Boolean emailNotifications = true;
    private Boolean smsNotifications = true;
    private Boolean pushNotifications = true;
    private Boolean whatsappNotifications = true;
    
    @ElementCollection
    @CollectionTable(name = "customer_frequent_destinations", 
                    joinColumns = @JoinColumn(name = "customer_id"))
    private List<CustomerAddress> frequentDestinations = new ArrayList<>();
    
    private String preferredVehicleType;
    
    // Ratings and Reviews
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    private Integer totalRatings = 0;
    
    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerReview> givenReviews = new ArrayList<>();
    
    // Complaints and Support
    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Complaint> complaints = new ArrayList<>();
    
    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupportTicket> supportTickets = new ArrayList<>();
    
    // Verification and Status
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    
    // Activity tracking
    private LocalDateTime lastLogin;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Enums
    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }
    
    public enum AccountStatus {
        ACTIVE, SUSPENDED, BLOCKED
    }
    
    // Embedded classes
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerBankDetails {
        private String accountNumber;
        private String accountHolderName;
        private String ifscCode;
        private String bankName;
        private String branchName;
        private String branchAddress;
        private String passbookPhoto;
        private String upiId;
        private String phonePayNumber;
        private Boolean isVerified = false;
        private LocalDateTime verificationDate;
    }
    // Explicit setters for minimal registration
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
