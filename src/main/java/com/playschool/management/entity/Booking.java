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
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String bookingNumber;
    
    // Customer, Vehicle, Owner, Driver
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    private String vehicleId;
    private String ownerId;
    private String driverId;
    
    // Cargo Details
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "description", column = @Column(name = "cargo_description")),
        @AttributeOverride(name = "type", column = @Column(name = "cargo_type")),
        @AttributeOverride(name = "weight", column = @Column(name = "cargo_weight")),
        @AttributeOverride(name = "length", column = @Column(name = "cargo_length")),
        @AttributeOverride(name = "width", column = @Column(name = "cargo_width")),
        @AttributeOverride(name = "height", column = @Column(name = "cargo_height")),
        @AttributeOverride(name = "value", column = @Column(name = "cargo_value")),
        @AttributeOverride(name = "specialInstructions", column = @Column(name = "cargo_special_instructions"))
    })
    private CargoDetails cargo;
    
    @ElementCollection
    @CollectionTable(name = "booking_cargo_photos", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "photo_url")
    private List<String> cargoPhotos = new ArrayList<>();
    
    // Pickup Details
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "pickup_street")),
        @AttributeOverride(name = "city", column = @Column(name = "pickup_city")),
        @AttributeOverride(name = "state", column = @Column(name = "pickup_state")),
        @AttributeOverride(name = "pincode", column = @Column(name = "pickup_pincode")),
        @AttributeOverride(name = "country", column = @Column(name = "pickup_country")),
        @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude"))
    })
    private BookingAddress pickupAddress;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "pickup_contact_name")),
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "pickup_contact_phone"))
    })
    private ContactPerson pickupContact;
    
    private LocalDateTime scheduledPickupDate;
    private String scheduledPickupTime;
    private LocalDateTime actualPickupTime;
    private String pickupInstructions;
    
    // Delivery Details
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
        @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
        @AttributeOverride(name = "state", column = @Column(name = "delivery_state")),
        @AttributeOverride(name = "pincode", column = @Column(name = "delivery_pincode")),
        @AttributeOverride(name = "country", column = @Column(name = "delivery_country")),
        @AttributeOverride(name = "latitude", column = @Column(name = "delivery_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "delivery_longitude"))
    })
    private BookingAddress deliveryAddress;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "delivery_contact_name")),
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "delivery_contact_phone"))
    })
    private ContactPerson deliveryContact;
    
    private LocalDateTime scheduledDeliveryDate;
    private String scheduledDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private String deliveryInstructions;
    private String deliveryPhoto;
    
    // Route Information
    @Column(precision = 8, scale = 2)
    private BigDecimal totalDistance; // in km
    
    private Integer estimatedDuration; // in minutes
    
    @Column(precision = 10, scale = 2)
    private BigDecimal tollCharges;
    
    // Pricing Details
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "baseFare", column = @Column(name = "pricing_base_fare")),
        @AttributeOverride(name = "perKmRate", column = @Column(name = "pricing_per_km_rate")),
        @AttributeOverride(name = "gstAmount", column = @Column(name = "pricing_gst_amount")),
        @AttributeOverride(name = "serviceCharge", column = @Column(name = "pricing_service_charge")),
        @AttributeOverride(name = "insuranceCharge", column = @Column(name = "pricing_insurance_charge")),
        @AttributeOverride(name = "tollCharges", column = @Column(name = "pricing_toll_charges")),
        @AttributeOverride(name = "totalAmount", column = @Column(name = "pricing_total_amount")),
        @AttributeOverride(name = "discountAmount", column = @Column(name = "pricing_discount_amount")),
        @AttributeOverride(name = "finalAmount", column = @Column(name = "pricing_final_amount"))
    })
    private PricingDetails pricing;
    
    // Payment Details
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "method", column = @Column(name = "payment_method")),
        @AttributeOverride(name = "status", column = @Column(name = "payment_status")),
        @AttributeOverride(name = "paidAmount", column = @Column(name = "payment_paid_amount")),
        @AttributeOverride(name = "transactionId", column = @Column(name = "payment_transaction_id")),
        @AttributeOverride(name = "paymentDate", column = @Column(name = "payment_date")),
        @AttributeOverride(name = "refundAmount", column = @Column(name = "payment_refund_amount")),
        @AttributeOverride(name = "refundDate", column = @Column(name = "payment_refund_date"))
    })
    private PaymentDetails payment;
    
    // Booking Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    // Tracking Information
    private Double currentLatitude;
    private Double currentLongitude;
    private String currentAddress;
    private LocalDateTime lastLocationUpdate;
    private LocalDateTime estimatedArrival;
    
    @OneToMany(mappedBy = "bookingId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingStatusUpdate> statusHistory = new ArrayList<>();
    
    // Communications
    @OneToMany(mappedBy = "bookingId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingMessage> communications = new ArrayList<>();
    
    // Cancellation Details
    private String cancellationReason;
    private String cancelledBy;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal cancellationFee;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    private LocalDateTime cancelledAt;
    
    // Terms and Conditions
    private Boolean customerTermsAccepted = false;
    private LocalDateTime customerTermsAcceptedAt;
    private Boolean ownerTermsAccepted = false;
    private LocalDateTime ownerTermsAcceptedAt;
    
    // Insurance Details
    private String insuranceProvider;
    private String insurancePolicyNumber;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal insuranceCoverageAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal insurancePremium;
    
    // Reviews
    private Integer customerRating;
    private String customerComment;
    private Integer driverRating;
    private String driverComment;
    private LocalDateTime reviewDate;
    
    // Admin Approval
    private Boolean adminApproved = false;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Enums
    public enum BookingStatus {
        PENDING, CONFIRMED, ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED, DISPUTED
    }
    
    public enum CargoType {
        GENERAL, FRAGILE, HAZARDOUS, PERISHABLE, VALUABLE
    }
    
    public enum PaymentMethod {
        CARD, UPI, NET_BANKING, WALLET, CASH
    }
    
    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }
    
    // Embedded classes
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CargoDetails {
        private String description;
        
        @Enumerated(EnumType.STRING)
        private CargoType type;
        
        @Column(precision = 8, scale = 2)
        private BigDecimal weight; // in kg
        
        @Column(precision = 6, scale = 2)
        private BigDecimal length;
        
        @Column(precision = 6, scale = 2)
        private BigDecimal width;
        
        @Column(precision = 6, scale = 2)
        private BigDecimal height;
        
        @Column(precision = 12, scale = 2)
        private BigDecimal value; // declared value for insurance
        
        @Column(length = 1000)
        private String specialInstructions;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public CargoType getType() {
			return type;
		}

		public void setType(CargoType type) {
			this.type = type;
		}

		public BigDecimal getWeight() {
			return weight;
		}

		public void setWeight(BigDecimal weight) {
			this.weight = weight;
		}

		public BigDecimal getLength() {
			return length;
		}

		public void setLength(BigDecimal length) {
			this.length = length;
		}

		public BigDecimal getWidth() {
			return width;
		}

		public void setWidth(BigDecimal width) {
			this.width = width;
		}

		public BigDecimal getHeight() {
			return height;
		}

		public void setHeight(BigDecimal height) {
			this.height = height;
		}

		public BigDecimal getValue() {
			return value;
		}

		public void setValue(BigDecimal value) {
			this.value = value;
		}

		public String getSpecialInstructions() {
			return specialInstructions;
		}

		public void setSpecialInstructions(String specialInstructions) {
			this.specialInstructions = specialInstructions;
		}
        
        
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingAddress {
        private String street;
        private String city;
        private String state;
        private String pincode;
        private String country;
        private Double latitude;
        private Double longitude;
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
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
        
        
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactPerson {
        private String name;
        private String phoneNumber;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingDetails {
        @Column(precision = 10, scale = 2)
        private BigDecimal baseFare;
        
        @Column(precision = 6, scale = 2)
        private BigDecimal perKmRate;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal gstAmount;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal serviceCharge;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal insuranceCharge;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal tollCharges;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal totalAmount;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal discountAmount;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal finalAmount;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetails {
        @Enumerated(EnumType.STRING)
        private PaymentMethod method;
        
        @Enumerated(EnumType.STRING)
        private PaymentStatus status;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal paidAmount;
        
        private String transactionId;
        private LocalDateTime paymentDate;
        
        @Column(precision = 10, scale = 2)
        private BigDecimal refundAmount;
        
        private LocalDateTime refundDate;
    }

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public CargoDetails getCargo() {
		return cargo;
	}

	public void setCargo(CargoDetails cargo) {
		this.cargo = cargo;
	}

	public LocalDateTime getScheduledPickupDate() {
		return scheduledPickupDate;
	}

	public void setScheduledPickupDate(LocalDateTime scheduledPickupDate) {
		this.scheduledPickupDate = scheduledPickupDate;
	}

	public LocalDateTime getActualPickupTime() {
		return actualPickupTime;
	}

	public void setActualPickupTime(LocalDateTime actualPickupTime) {
		this.actualPickupTime = actualPickupTime;
	}

	public LocalDateTime getScheduledDeliveryDate() {
		return scheduledDeliveryDate;
	}

	public void setScheduledDeliveryDate(LocalDateTime scheduledDeliveryDate) {
		this.scheduledDeliveryDate = scheduledDeliveryDate;
	}

	public String getScheduledDeliveryTime() {
		return scheduledDeliveryTime;
	}

	public void setScheduledDeliveryTime(String scheduledDeliveryTime) {
		this.scheduledDeliveryTime = scheduledDeliveryTime;
	}

	public LocalDateTime getActualDeliveryTime() {
		return actualDeliveryTime;
	}

	public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
		this.actualDeliveryTime = actualDeliveryTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBookingNumber() {
		return bookingNumber;
	}

	public void setBookingNumber(String bookingNumber) {
		this.bookingNumber = bookingNumber;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public List<String> getCargoPhotos() {
		return cargoPhotos;
	}

	public void setCargoPhotos(List<String> cargoPhotos) {
		this.cargoPhotos = cargoPhotos;
	}

	public BookingAddress getPickupAddress() {
		return pickupAddress;
	}

	public void setPickupAddress(BookingAddress pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	public ContactPerson getPickupContact() {
		return pickupContact;
	}

	public void setPickupContact(ContactPerson pickupContact) {
		this.pickupContact = pickupContact;
	}

	public String getScheduledPickupTime() {
		return scheduledPickupTime;
	}

	public void setScheduledPickupTime(String scheduledPickupTime) {
		this.scheduledPickupTime = scheduledPickupTime;
	}

	public String getPickupInstructions() {
		return pickupInstructions;
	}

	public void setPickupInstructions(String pickupInstructions) {
		this.pickupInstructions = pickupInstructions;
	}

	public BookingAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(BookingAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public ContactPerson getDeliveryContact() {
		return deliveryContact;
	}

	public void setDeliveryContact(ContactPerson deliveryContact) {
		this.deliveryContact = deliveryContact;
	}

	public String getDeliveryInstructions() {
		return deliveryInstructions;
	}

	public void setDeliveryInstructions(String deliveryInstructions) {
		this.deliveryInstructions = deliveryInstructions;
	}

	public String getDeliveryPhoto() {
		return deliveryPhoto;
	}

	public void setDeliveryPhoto(String deliveryPhoto) {
		this.deliveryPhoto = deliveryPhoto;
	}

	public BigDecimal getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(BigDecimal totalDistance) {
		this.totalDistance = totalDistance;
	}

	public Integer getEstimatedDuration() {
		return estimatedDuration;
	}

	public void setEstimatedDuration(Integer estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}

	public BigDecimal getTollCharges() {
		return tollCharges;
	}

	public void setTollCharges(BigDecimal tollCharges) {
		this.tollCharges = tollCharges;
	}

	public PricingDetails getPricing() {
		return pricing;
	}

	public void setPricing(PricingDetails pricing) {
		this.pricing = pricing;
	}

	public PaymentDetails getPayment() {
		return payment;
	}

	public void setPayment(PaymentDetails payment) {
		this.payment = payment;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public Double getCurrentLatitude() {
		return currentLatitude;
	}

	public void setCurrentLatitude(Double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	public Double getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(Double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}

	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}

	public LocalDateTime getLastLocationUpdate() {
		return lastLocationUpdate;
	}

	public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
		this.lastLocationUpdate = lastLocationUpdate;
	}

	public LocalDateTime getEstimatedArrival() {
		return estimatedArrival;
	}

	public void setEstimatedArrival(LocalDateTime estimatedArrival) {
		this.estimatedArrival = estimatedArrival;
	}

	public List<BookingStatusUpdate> getStatusHistory() {
		return statusHistory;
	}

	public void setStatusHistory(List<BookingStatusUpdate> statusHistory) {
		this.statusHistory = statusHistory;
	}

	public List<BookingMessage> getCommunications() {
		return communications;
	}

	public void setCommunications(List<BookingMessage> communications) {
		this.communications = communications;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public BigDecimal getCancellationFee() {
		return cancellationFee;
	}

	public void setCancellationFee(BigDecimal cancellationFee) {
		this.cancellationFee = cancellationFee;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public LocalDateTime getCancelledAt() {
		return cancelledAt;
	}

	public void setCancelledAt(LocalDateTime cancelledAt) {
		this.cancelledAt = cancelledAt;
	}

	public Boolean getCustomerTermsAccepted() {
		return customerTermsAccepted;
	}

	public void setCustomerTermsAccepted(Boolean customerTermsAccepted) {
		this.customerTermsAccepted = customerTermsAccepted;
	}

	public LocalDateTime getCustomerTermsAcceptedAt() {
		return customerTermsAcceptedAt;
	}

	public void setCustomerTermsAcceptedAt(LocalDateTime customerTermsAcceptedAt) {
		this.customerTermsAcceptedAt = customerTermsAcceptedAt;
	}

	public Boolean getOwnerTermsAccepted() {
		return ownerTermsAccepted;
	}

	public void setOwnerTermsAccepted(Boolean ownerTermsAccepted) {
		this.ownerTermsAccepted = ownerTermsAccepted;
	}

	public LocalDateTime getOwnerTermsAcceptedAt() {
		return ownerTermsAcceptedAt;
	}

	public void setOwnerTermsAcceptedAt(LocalDateTime ownerTermsAcceptedAt) {
		this.ownerTermsAcceptedAt = ownerTermsAcceptedAt;
	}

	public String getInsuranceProvider() {
		return insuranceProvider;
	}

	public void setInsuranceProvider(String insuranceProvider) {
		this.insuranceProvider = insuranceProvider;
	}

	public String getInsurancePolicyNumber() {
		return insurancePolicyNumber;
	}

	public void setInsurancePolicyNumber(String insurancePolicyNumber) {
		this.insurancePolicyNumber = insurancePolicyNumber;
	}

	public BigDecimal getInsuranceCoverageAmount() {
		return insuranceCoverageAmount;
	}

	public void setInsuranceCoverageAmount(BigDecimal insuranceCoverageAmount) {
		this.insuranceCoverageAmount = insuranceCoverageAmount;
	}

	public BigDecimal getInsurancePremium() {
		return insurancePremium;
	}

	public void setInsurancePremium(BigDecimal insurancePremium) {
		this.insurancePremium = insurancePremium;
	}

	public Integer getCustomerRating() {
		return customerRating;
	}

	public void setCustomerRating(Integer customerRating) {
		this.customerRating = customerRating;
	}

	public String getCustomerComment() {
		return customerComment;
	}

	public void setCustomerComment(String customerComment) {
		this.customerComment = customerComment;
	}

	public Integer getDriverRating() {
		return driverRating;
	}

	public void setDriverRating(Integer driverRating) {
		this.driverRating = driverRating;
	}

	public String getDriverComment() {
		return driverComment;
	}

	public void setDriverComment(String driverComment) {
		this.driverComment = driverComment;
	}

	public LocalDateTime getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(LocalDateTime reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Boolean getAdminApproved() {
		return adminApproved;
	}

	public void setAdminApproved(Boolean adminApproved) {
		this.adminApproved = adminApproved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
}
