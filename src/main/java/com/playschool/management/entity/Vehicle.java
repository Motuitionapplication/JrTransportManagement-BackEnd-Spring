
package com.playschool.management.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = true)
    private String vehicleNumber = "";
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private VehicleType vehicleType;
    
    @Column(nullable = true)
    private String model = "";
    
    @Column(nullable = true)
    private String manufacturer = "";
    
    public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Column(name = "`year`", nullable = true)
    private Integer year;
    
    @DecimalMin(value = "0.0", message = "Capacity must be positive")
    @Column(nullable = true)
    private BigDecimal capacity;
    
    public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public BigDecimal getCapacity() {
		return capacity;
	}

	public void setCapacity(BigDecimal capacity) {
		this.capacity = capacity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private VehicleOwner owner;
    
    @Column(nullable = true)
    private String driverId = "";
    
    // Document details as embedded objects
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "number", column = @Column(name = "registration_number")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "registration_expiry")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "registration_document_url"))
    })
    private DocumentInfo registration;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "number", column = @Column(name = "insurance_policy_number")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "insurance_expiry")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "insurance_document_url")),
        @AttributeOverride(name = "provider", column = @Column(name = "insurance_provider"))
    })
    private InsuranceInfo insurance;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "number", column = @Column(name = "permit_number")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "permit_expiry")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "permit_document_url"))
    })
    private DocumentInfo permit;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "number", column = @Column(name = "fitness_certificate_number")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "fitness_expiry")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "fitness_document_url"))
    })
    private DocumentInfo fitness;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "number", column = @Column(name = "pollution_certificate_number")),
        @AttributeOverride(name = "expiryDate", column = @Column(name = "pollution_expiry")),
        @AttributeOverride(name = "documentUrl", column = @Column(name = "pollution_document_url"))
    })
    private DocumentInfo pollution;
    
    // Current location
    @Column(nullable = true)
    private Double currentLatitude;
    @Column(nullable = true)
    private Double currentLongitude;
    @Column(nullable = true)
    private String currentAddress = "";
    @Column(nullable = true)
    private LocalDateTime lastLocationUpdate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private VehicleStatus status = VehicleStatus.AVAILABLE;
    
    // Fare details
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "perKmRate", column = @Column(name = "per_km_rate")),
        @AttributeOverride(name = "wholeFare", column = @Column(name = "whole_fare")),
        @AttributeOverride(name = "sharingFare", column = @Column(name = "sharing_fare")),
        @AttributeOverride(name = "gstIncluded", column = @Column(name = "gst_included")),
        @AttributeOverride(name = "movingInsurance", column = @Column(name = "moving_insurance"))
    })
    private FareDetails fareDetails;
    
    // Maintenance records (separate entity) - Remove bidirectional mapping to avoid DDL issues
    // @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<MaintenanceRecord> maintenanceHistory = new ArrayList<>();
    
    @Column(nullable = true)
    private LocalDate nextServiceDate;
    
    @CreationTimestamp
    @Column(nullable = true)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = true)
    private LocalDateTime updatedAt;
    
    @Column(nullable = true)
    private Boolean isActive = true;
    
    @Column(nullable = true)
    private Boolean isVerified = false;
    
    // Enums
    public enum VehicleType {
        TRUCK, VAN, TRAILER, CONTAINER, PICKUP
    }
    
    public enum VehicleStatus {
        AVAILABLE, IN_TRANSIT, MAINTENANCE, INACTIVE
    }
    
    // Embedded classes
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        private String number;
        private LocalDate expiryDate;
        private String documentUrl;
        
        // Explicit getter for compatibility
        public LocalDate getExpiryDate() {
            return this.expiryDate;
        }
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsuranceInfo {
        private String number;
        private LocalDate expiryDate;
        private String documentUrl;
        private String provider;
        
        // Explicit getter for compatibility
        public LocalDate getExpiryDate() {
            return this.expiryDate;
        }
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FareDetails {
        private BigDecimal perKmRate;
        private BigDecimal wholeFare;
        private BigDecimal sharingFare;
        private Boolean gstIncluded = true;
        private BigDecimal movingInsurance;
    }
    
    // Explicit getters and setters for compatibility
    public String getId() {
        return this.id;
    }
    
    public String getVehicleNumber() {
        return this.vehicleNumber;
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
    
    public VehicleStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
    
    public Boolean getIsActive() {
        return this.isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Double getCurrentLatitude() {
        return this.currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    
    public Double getCurrentLongitude() {
        return this.currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
    
    public String getCurrentAddress() {
        return this.currentAddress;
    }
    
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }
    
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }
    
    public LocalDate getNextServiceDate() {
        return this.nextServiceDate;
    }
    
    public void setNextServiceDate(LocalDate nextServiceDate) {
        this.nextServiceDate = nextServiceDate;
    }
    
    public DocumentInfo getRegistration() {
        return this.registration;
    }
    
    public InsuranceInfo getInsurance() {
        return this.insurance;
    }
    
    public DocumentInfo getPollution() {
        return this.pollution;
    }
    
    public DocumentInfo getFitness() {
        return this.fitness;
    }
    
    public DocumentInfo getPermit() {
        return this.permit;
    }
    
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

	public VehicleOwner getOwner() {
		return owner;
	}

	public void setOwner(VehicleOwner owner) {
		this.owner = owner;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	
    
}
