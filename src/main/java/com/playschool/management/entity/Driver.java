package com.playschool.management.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "drivers")
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    
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
    private String profilePhoto;
    
    @NotBlank(message = "Blood group is required")
    private String bloodGroup;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "emergency_contact_name")),
        @AttributeOverride(name = "relationship", column = @Column(name = "emergency_contact_relationship")),
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "emergency_contact_phone"))
    })
    private EmergencyContact emergencyContact;
    
    @Column(unique = true, nullable = false)
    private String userId;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // --- FIX: Restored @AttributeOverrides ---
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
    
    // --- FIX: Restored @AttributeOverrides ---
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "type", column = @Column(name = "identity_type")),
        @AttributeOverride(name = "number", column = @Column(name = "identity_number")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "identity_document_url")),
        @AttributeOverride(name = "isVerified", column = @Column(name = "identity_verified")),
        @AttributeOverride(name = "verificationDate", column = @Column(name = "identity_verification_date"))
    })
    private VehicleOwner.IdentityProof identityProof;
    
    // --- FIX: Restored @AttributeOverrides ---
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "policyNumber", column = @Column(name = "insurance_policy_number")),
        @AttributeOverride(name = "provider", column = @Column(name = "insurance_provider")),
        @AttributeOverride(name = "coverageAmount", column = @Column(name = "insurance_coverage_amount")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "insurance_expiry_date")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "insurance_document_url"))
    })
    private DriverInsurance insurance;
    
    @ElementCollection
    @CollectionTable(name = "driver_assigned_vehicles", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "vehicle_id")
    private List<String> assignedVehicles = new ArrayList<>();
    
    private String currentVehicle;
    
    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.AVAILABLE;
    
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
    
    private Integer totalYearsExperience;
    
    @ElementCollection
    @CollectionTable(name = "driver_previous_employers", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "employer")
    private List<String> previousEmployers = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "driver_specializations", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "specialization")
    private List<String> specializations = new ArrayList<>();
    
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    private Integer totalRatings = 0;
    
    @OneToMany(mappedBy = "driverId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DriverReview> reviews = new ArrayList<>();
    
    private Integer currentRewardPoints = 0;
    private Integer totalEarnedPoints = 0;
    private Integer redeemedPoints = 0;
    
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RewardTransaction> rewardTransactions = new ArrayList<>();
    
    @OneToMany(mappedBy = "addedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceCenter> serviceCenters = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "driver_trip_history", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "trip_id")
    private List<String> tripHistory = new ArrayList<>();
    
    private Integer totalTripsCompleted = 0;
    
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    private BackgroundCheckStatus backgroundCheckStatus = BackgroundCheckStatus.PENDING;
    
    private LocalDateTime lastLogin;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_owner_id")
    @JsonBackReference
    private VehicleOwner vehicleOwner;

    public Driver() {
    }

    // Getters and Setters ...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAlternatePhone() { return alternatePhone; }
    public void setAlternatePhone(String alternatePhone) { this.alternatePhone = alternatePhone; }
    public VehicleOwner.Address getAddress() { return address; }
    public void setAddress(VehicleOwner.Address address) { this.address = address; }
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public EmergencyContact getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(EmergencyContact emergencyContact) { this.emergencyContact = emergencyContact; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public DrivingLicense getDrivingLicense() { return drivingLicense; }
    public void setDrivingLicense(DrivingLicense drivingLicense) { this.drivingLicense = drivingLicense; }
    public VehicleOwner.IdentityProof getIdentityProof() { return identityProof; }
    public void setIdentityProof(VehicleOwner.IdentityProof identityProof) { this.identityProof = identityProof; }
    public DriverInsurance getInsurance() { return insurance; }
    public void setInsurance(DriverInsurance insurance) { this.insurance = insurance; }
    public List<String> getAssignedVehicles() { return assignedVehicles; }
    public void setAssignedVehicles(List<String> assignedVehicles) { this.assignedVehicles = assignedVehicles; }
    public String getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(String currentVehicle) { this.currentVehicle = currentVehicle; }
    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }
    public WorkingHours getWorkingHours() { return workingHours; }
    public void setWorkingHours(WorkingHours workingHours) { this.workingHours = workingHours; }
    public List<String> getWorkingDays() { return workingDays; }
    public void setWorkingDays(List<String> workingDays) { this.workingDays = workingDays; }
    public Integer getTotalYearsExperience() { return totalYearsExperience; }
    public void setTotalYearsExperience(Integer totalYearsExperience) { this.totalYearsExperience = totalYearsExperience; }
    public List<String> getPreviousEmployers() { return previousEmployers; }
    public void setPreviousEmployers(List<String> previousEmployers) { this.previousEmployers = previousEmployers; }
    public List<String> getSpecializations() { return specializations; }
    public void setSpecializations(List<String> specializations) { this.specializations = specializations; }
    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
    public Integer getTotalRatings() { return totalRatings; }
    public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }
    public List<DriverReview> getReviews() { return reviews; }
    public void setReviews(List<DriverReview> reviews) { this.reviews = reviews; }
    public Integer getCurrentRewardPoints() { return currentRewardPoints; }
    public void setCurrentRewardPoints(Integer currentRewardPoints) { this.currentRewardPoints = currentRewardPoints; }
    public Integer getTotalEarnedPoints() { return totalEarnedPoints; }
    public void setTotalEarnedPoints(Integer totalEarnedPoints) { this.totalEarnedPoints = totalEarnedPoints; }
    public Integer getRedeemedPoints() { return redeemedPoints; }
    public void setRedeemedPoints(Integer redeemedPoints) { this.redeemedPoints = redeemedPoints; }
    public List<RewardTransaction> getRewardTransactions() { return rewardTransactions; }
    public void setRewardTransactions(List<RewardTransaction> rewardTransactions) { this.rewardTransactions = rewardTransactions; }
    public List<ServiceCenter> getServiceCenters() { return serviceCenters; }
    public void setServiceCenters(List<ServiceCenter> serviceCenters) { this.serviceCenters = serviceCenters; }
    public List<String> getTripHistory() { return tripHistory; }
    public void setTripHistory(List<String> tripHistory) { this.tripHistory = tripHistory; }
    public Integer getTotalTripsCompleted() { return totalTripsCompleted; }
    public void setTotalTripsCompleted(Integer totalTripsCompleted) { this.totalTripsCompleted = totalTripsCompleted; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }
    public BackgroundCheckStatus getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(BackgroundCheckStatus backgroundCheckStatus) { this.backgroundCheckStatus = backgroundCheckStatus; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public VehicleOwner getVehicleOwner() { return vehicleOwner; }
    public void setVehicleOwner(VehicleOwner vehicleOwner) { this.vehicleOwner = vehicleOwner; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return Objects.equals(id, driver.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Driver{" + "id='" + id + '\'' + ", firstName='" + firstName + '\'' + '}';
    }

    // Enums ...
    public enum DriverStatus { AVAILABLE, ON_TRIP, OFF_DUTY, BREAK }
    public enum VerificationStatus { PENDING, VERIFIED, REJECTED }
    public enum AccountStatus { ACTIVE, SUSPENDED, BLOCKED }
    public enum BackgroundCheckStatus { PENDING, CLEARED, FAILED }
    public enum LicenseType { LMV, HMV, HPMV, HGMV, PSV, TRANSPORT }
    
    // Embedded classes ...
    @Embeddable
    public static class EmergencyContact {
        private String name;
        private String relationship;
        private String phoneNumber;
        public EmergencyContact() {}
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
    
    @Embeddable
    public static class DrivingLicense {
        private String licenseNumber;
        @Enumerated(EnumType.STRING)
        private LicenseType licenseType;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String issuingAuthority;
        private String documentUrl;
        private Boolean isVerified = false;
        public DrivingLicense() {}
        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
        public LicenseType getLicenseType() { return licenseType; }
        public void setLicenseType(LicenseType licenseType) { this.licenseType = licenseType; }
        public LocalDate getIssueDate() { return issueDate; }
        public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
        public String getIssuingAuthority() { return issuingAuthority; }
        public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }
        public String getDocumentUrl() { return documentUrl; }
        public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
        public Boolean getIsVerified() { return isVerified; }
        public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    }
    
    @Embeddable
    public static class DriverInsurance {
        private String policyNumber;
        private String provider;
        private BigDecimal coverageAmount;
        private LocalDate expiryDate;
        private String documentUrl;
        public DriverInsurance() {}
        public String getPolicyNumber() { return policyNumber; }
        public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public BigDecimal getCoverageAmount() { return coverageAmount; }
        public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
        public String getDocumentUrl() { return documentUrl; }
        public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    }
    
    @Embeddable
    public static class WorkingHours {
        private String startTime;
        private String endTime;
        public WorkingHours() {}
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }
}
