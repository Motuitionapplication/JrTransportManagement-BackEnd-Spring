package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "vehicle_owners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleOwner {
    
    private static final Logger log = LoggerFactory.getLogger(VehicleOwner.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    // Profile Information
    private String firstName = "";
    
    private String lastName = "";
    
    @Column(unique = true, nullable = true)
    private String email = "";
    
    @Column(unique = true, nullable = true)
    private String phoneNumber = "";
    
    private String alternatePhone = "";
    
    
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
    // Address
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "address_street")),
        @AttributeOverride(name = "city", column = @Column(name = "address_city")),
        @AttributeOverride(name = "state", column = @Column(name = "address_state")),
        @AttributeOverride(name = "pincode", column = @Column(name = "address_pincode")),
        @AttributeOverride(name = "country", column = @Column(name = "address_country"))
    })
    private Address address;
    
    private String profilePhoto = "";
    
    // Authentication
    @Column(unique = true, nullable = true)
    private String userId = "";
    
    private String password = ""; // This will be hashed
    
    // Business Details
    private String companyName = "";
    private String gstNumber = "";
    
    private String panNumber = "";
    
    // Additional properties that services are expecting
    private String ownerId = ""; // This might be needed for compatibility
    private BigDecimal averageRating = BigDecimal.ZERO;
    private Integer totalRatings = 0;
    
    @Column(name = "owner_verification_notes", nullable = true)
    private String verificationNotes = "";
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "companyName", column = @Column(name = "business_company_name")),
        @AttributeOverride(name = "gstNumber", column = @Column(name = "business_gst_number")),
        @AttributeOverride(name = "panNumber", column = @Column(name = "business_pan_number")),
        @AttributeOverride(name = "businessType", column = @Column(name = "business_type")),
        @AttributeOverride(name = "registrationNumber", column = @Column(name = "business_registration_number")),
        @AttributeOverride(name = "incorporationDate", column = @Column(name = "business_incorporation_date")),
        @AttributeOverride(name = "isVerified", column = @Column(name = "business_verified")),
        @AttributeOverride(name = "verificationDate", column = @Column(name = "business_verification_date")),
        @AttributeOverride(name = "verificationNotes", column = @Column(name = "business_verification_notes"))
    })
    private BusinessDetails businessDetails;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "business_street")),
        @AttributeOverride(name = "city", column = @Column(name = "business_city")),
        @AttributeOverride(name = "state", column = @Column(name = "business_state")),
        @AttributeOverride(name = "pincode", column = @Column(name = "business_pincode")),
        @AttributeOverride(name = "country", column = @Column(name = "business_country"))
    })
    private Address businessAddress;
    
    // Identity Proof
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "type", column = @Column(name = "identity_type")),
        @AttributeOverride(name = "number", column = @Column(name = "identity_number")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "identity_document_url")),
        @AttributeOverride(name = "isVerified", column = @Column(name = "identity_verified")),
        @AttributeOverride(name = "verificationDate", column = @Column(name = "identity_verification_date"))
    })
    private IdentityProof identityProof;
    
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
        @AttributeOverride(name = "isVerified", column = @Column(name = "bank_verified"))
    })
    private BankDetails bankDetails;
    
    // Vehicle relationships
    @OneToMany(mappedBy = "ownerId", fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();
    
    // Wallet
    @Column(precision = 10, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal reservedAmount = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "ownerId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WalletTransaction> walletTransactions = new ArrayList<>();
    
    // Service Preferences
    private Boolean emailNotifications = true;
    private Boolean smsNotifications = true;
    private Boolean pushNotifications = true;
    private Boolean trackingEnabled = true;
    private Boolean autoAcceptOrders = false;
    
    // Status and Verification
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
    public enum BusinessType {
        INDIVIDUAL, PARTNERSHIP, COMPANY
    }
    
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
    public static class Address {
        public String getStreet() {
            return street;
        }
        public void setStreet(String street) {
            this.street = street;
        }
        public String getCity() {
            return city;
        }
        public void setCity(String city) {
            this.city = city;
        }
        public String getState() {
            return state;
        }
        public void setState(String state) {
            this.state = state;
        }
        public String getPincode() {
            return pincode;
        }
        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }
        private String street;
        private String city;
        private String state;
        private String pincode;
        private String country;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentityProof {
        @Enumerated(EnumType.STRING)
        private IdentityType type;
        private String number;
        private String documentUrl;
        private Boolean isVerified = false;
        private LocalDateTime verificationDate;
        
        public enum IdentityType {
            AADHAR, VOTER_ID, PAN_CARD, PASSPORT, GOVT_ID, DRIVING_LICENSE
        }
        
        // Explicit setters for service compatibility
        public void setIsVerified(boolean isVerified) {
            this.isVerified = isVerified;
        }
        
        public void setVerificationDate(LocalDateTime verificationDate) {
            this.verificationDate = verificationDate;
        }
        
        public void setVerificationNotes(String verificationNotes) {
            // This could be added to the class if needed
        }
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankDetails {
        private String accountNumber;
        private String accountHolderName;
        private String ifscCode;
        private String bankName;
        private String branchName;
        private String branchAddress;
        private String passbookPhoto;
        private Boolean isVerified = false;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessDetails {
        private String companyName;
        private String gstNumber;
        private String panNumber;
        private BusinessType businessType;
        private String registrationNumber;
        private LocalDate incorporationDate;
        private Boolean isVerified = false;
        private LocalDateTime verificationDate;
        private String verificationNotes;
        
        // Explicit setters for service compatibility
        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }
        
        public void setGstNumber(String gstNumber) {
            this.gstNumber = gstNumber;
        }
        
        public void setPanNumber(String panNumber) {
            this.panNumber = panNumber;
        }
        
        public void setBusinessType(BusinessType businessType) {
            this.businessType = businessType;
        }
        
        public void setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }
        
        public void setIncorporationDate(LocalDate incorporationDate) {
            this.incorporationDate = incorporationDate;
        }
        
        public void setIsVerified(boolean isVerified) {
            this.isVerified = isVerified;
        }
        
        public void setVerificationDate(LocalDateTime verificationDate) {
            this.verificationDate = verificationDate;
        }
        
        public void setVerificationNotes(String verificationNotes) {
            this.verificationNotes = verificationNotes;
        }
    }
    
    // Explicit getters for compatibility
    public String getOwnerId() {
        return this.ownerId != null ? this.ownerId : this.id;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public BigDecimal getAverageRating() {
        return this.averageRating;
    }
    
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getTotalRatings() {
        return this.totalRatings;
    }
    
    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }
    
    public String getVerificationNotes() {
        return this.verificationNotes;
    }
    
    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }
    
    public VerificationStatus getVerificationStatus() {
        return this.verificationStatus;
    }
    
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    
    public AccountStatus getAccountStatus() {
        return this.accountStatus;
    }
    
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
    
    public BigDecimal getWalletBalance() {
        return this.walletBalance;
    }
    
    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }
    
    public List<WalletTransaction> getWalletTransactions() {
        return this.walletTransactions;
    }
    
    public IdentityProof getIdentityProof() {
        return this.identityProof;
    }
    
    public BusinessDetails getBusinessDetails() {
        if (this.businessDetails != null) {
            return this.businessDetails;
        }
        
        // Fallback for backward compatibility
        BusinessDetails details = new BusinessDetails();
        details.setCompanyName(this.companyName);
        details.setGstNumber(this.gstNumber);
        details.setPanNumber(this.panNumber);
        details.setBusinessType(null); // Will be set through businessDetails field
        details.setRegistrationNumber(null);
        details.setIncorporationDate(null);
        details.setIsVerified(false);
        details.setVerificationDate(null);
        details.setVerificationNotes(null);
        return details;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
