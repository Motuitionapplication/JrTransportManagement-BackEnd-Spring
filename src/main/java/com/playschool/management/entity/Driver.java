package com.playschool.management.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    // Profile Information
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Father/Husband name is required")
    private String fatherName;
    
    @Email(message = "Valid email is required")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Phone number is required")
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
    
    @NotBlank(message = "Profile photo is required")
    private String profilePhoto; // captured with eye blinking verification
    
    @NotBlank(message = "Blood group is required")
    private String bloodGroup;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    
    // Emergency Contact
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "emergency_contact_name")),
        @AttributeOverride(name = "relationship", column = @Column(name = "emergency_contact_relationship")),
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "emergency_contact_phone"))
    })
    private EmergencyContact emergencyContact;
    
    // Authentication
    @Column(unique = true, nullable = false)
    private String userId;
    
    @NotBlank(message = "Password is required")
    private String password; // This will be hashed
    
    // Driving License
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "licenseNumber", column = @Column(name = "license_number")),
        @AttributeOverride(name = "licenseType", column = @Column(name = "license_type")),
        @AttributeOverride(name = "issueDate", column = @Column(name = "license_issue_date")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "license_expiry_date")),
        @AttributeOverride(name = "issuingAuthority", column = @Column(name = "license_issuing_authority")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "license_document_url")),
        @AttributeOverride(name = "isVerified", column = @Column(name = "license_verified"))
    })
    private DrivingLicense drivingLicense;
    
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
    
    // Insurance
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "policyNumber", column = @Column(name = "insurance_policy_number")),
        @AttributeOverride(name = "provider", column = @Column(name = "insurance_provider")),
        @AttributeOverride(name = "coverageAmount", column = @Column(name = "insurance_coverage_amount")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "insurance_expiry_date")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "insurance_document_url"))
    })
    private DriverInsurance insurance;
    
    // Vehicle Assignment
    @ElementCollection
    @CollectionTable(name = "driver_assigned_vehicles", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "vehicle_id")
    private List<String> assignedVehicles = new ArrayList<>();
    
    private String currentVehicle;
    
    // Driver Status
    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.AVAILABLE;
    
    // Working Hours
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startTime", column = @Column(name = "work_start_time")),
        @AttributeOverride(name = "endTime", column = @Column(name = "work_end_time"))
    })
    private WorkingHours workingHours;
    
    @ElementCollection
    @CollectionTable(name = "driver_working_days", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "working_day")
    private List<String> workingDays = new ArrayList<>();
    
    // Experience and Ratings
    private Integer totalYearsExperience;
    
    @ElementCollection
    @CollectionTable(name = "driver_previous_employers", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "employer")
    private List<String> previousEmployers = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "driver_specializations", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "specialization")
    private List<String> specializations = new ArrayList<>();
    
    // Ratings
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    private Integer totalRatings = 0;
    
    @OneToMany(mappedBy = "driverId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DriverReview> reviews = new ArrayList<>();
    
    // Reward Points
    private Integer currentRewardPoints = 0;
    private Integer totalEarnedPoints = 0;
    private Integer redeemedPoints = 0;
    
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RewardTransaction> rewardTransactions = new ArrayList<>();
    
    // Service Centers
    @OneToMany(mappedBy = "addedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceCenter> serviceCenters = new ArrayList<>();
    
    // Trip History
    @ElementCollection
    @CollectionTable(name = "driver_trip_history", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "trip_id")
    private List<String> tripHistory = new ArrayList<>();
    
    private Integer totalTripsCompleted = 0;
    
    // Verification and Status
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    private BackgroundCheckStatus backgroundCheckStatus = BackgroundCheckStatus.PENDING;
    
    // Activity tracking
    private LocalDateTime lastLogin;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Enums
    public enum DriverStatus {
        AVAILABLE, ON_TRIP, OFF_DUTY, BREAK
    }
    
    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }
    
    public enum AccountStatus {
        ACTIVE, SUSPENDED, BLOCKED
    }
    
    public enum BackgroundCheckStatus {
        PENDING, CLEARED, FAILED
    }
    
    public enum LicenseType {
        LMV, HMV, HPMV, HGMV, PSV, TRANSPORT
    }
    
    // Embedded classes
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContact {
        private String name;
        private String relationship;
        private String phoneNumber;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrivingLicense {
        private String licenseNumber;
        @Enumerated(EnumType.STRING)
        private LicenseType licenseType;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String issuingAuthority;
        private String documentUrl;
        private Boolean isVerified = false;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverInsurance {
        private String policyNumber;
        private String provider;
        private BigDecimal coverageAmount;
        private LocalDate expiryDate;
        private String documentUrl;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingHours {
        private String startTime; // HH:MM format
        private String endTime;   // HH:MM format
    }
}
