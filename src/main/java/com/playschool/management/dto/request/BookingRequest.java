package com.playschool.management.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class BookingRequest {
	    
	    // ===== ROUTE DETAILS (Step 1) =====
	    @NotBlank(message = "Pickup location is required")
	    @Size(min = 3, max = 500, message = "Pickup location must be between 3 and 500 characters")
	    private String pickupLocation;
	    
	    @NotBlank(message = "Dropoff location is required")
	    @Size(min = 3, max = 500, message = "Dropoff location must be between 3 and 500 characters")
	    private String dropoffLocation;
	    
	    @NotNull(message = "Pickup date is required")
	    @Future(message = "Pickup date must be in the future")
	    private LocalDate pickupDate;
	    
	    @NotNull(message = "Pickup time is required")
	    private LocalTime pickupTime;
	    
	    // ===== GOODS DETAILS (Step 2) =====
	    @NotBlank(message = "Goods type is required")
	    @Size(max = 100, message = "Goods type cannot exceed 100 characters")
	    private String goodsType; // Maps to cargo.goodsCategory
	    
	    @NotNull(message = "Goods weight is required")
	    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
	    @DecimalMax(value = "100000.00", message = "Weight cannot exceed 100000 kg")
	    private BigDecimal goodsWeight; // Maps to cargo.weight
	    
	    @NotBlank(message = "Goods description is required")
	    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
	    private String goodsDescription; // Maps to cargo.description
	    
	    private Boolean specialHandling = false; // Maps to cargo.requiresSpecialHandling
	    
	    private Boolean fragile = false; // Maps to cargo.fragile
	    
	    private Boolean perishable = false; // Maps to cargo.perishable
	    
	    // ===== VEHICLE SELECTION (Step 3) =====
	    @NotBlank(message = "Vehicle type is required")
	    @Pattern(regexp = "^(truck|van|pickup|heavy-truck)$", message = "Invalid vehicle type")
	    private String selectedVehicleType; // Maps to vehicleType
	    
	    private String vehicleId; // Maps to vehicleId
	    
	    @Size(max = 100, message = "Vehicle name cannot exceed 100 characters")
	    private String vehicleName; // Maps to vehicleName
	    
	    @Min(value = 1, message = "Vehicle capacity must be at least 1 kg")
	    @Max(value = 100000, message = "Vehicle capacity cannot exceed 100000 kg")
	    private Integer vehicleCapacity; // Maps to vehicleCapacity
	    
	    // ===== CONTACT & PAYMENT (Step 4) =====
	    @NotBlank(message = "Contact name is required")
	    @Size(min = 3, max = 100, message = "Contact name must be between 3 and 100 characters")
	    private String contactName; // Maps to pickupContact.name & deliveryContact.name
	    
	    @NotBlank(message = "Contact phone is required")
	    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
	    private String contactPhone; // Maps to pickupContact.phoneNumber & deliveryContact.phoneNumber
	    
	    @NotBlank(message = "Contact email is required")
	    @Email(message = "Invalid email format")
	    @Size(max = 100, message = "Email cannot exceed 100 characters")
	    private String contactEmail; // Maps to pickupContact.email & deliveryContact.email
	    
	    @Size(max = 2000, message = "Additional notes cannot exceed 2000 characters")
	    private String additionalNotes; // Maps to bookingNotes
	    
	    @NotBlank(message = "Payment method is required")
	    @Pattern(regexp = "^(credit_card|debit_card|upi|net_banking|cash)$", message = "Invalid payment method")
	    private String paymentMethod; // Maps to payment.method
	    
	    // ===== PRICING INFORMATION =====
	    @NotNull(message = "Base price is required")
	    @DecimalMin(value = "0.00", message = "Base price must be positive")
	    private BigDecimal basePrice; // Maps to pricing.baseFare
	    
	    @DecimalMin(value = "0.00", message = "Insurance cost must be positive")
	    private BigDecimal insuranceCost; // Maps to pricing.insuranceCharge
	    
	    @DecimalMin(value = "0.00", message = "Tax amount must be positive")
	    private BigDecimal taxAmount; // Maps to pricing.gstAmount
	    
	    @NotNull(message = "Total amount is required")
	    @DecimalMin(value = "0.01", message = "Total amount must be greater than zero")
	    private BigDecimal totalAmount; // Maps to pricing.totalAmount & pricing.finalAmount
	    
	    @DecimalMin(value = "0.0", message = "Distance must be positive")
	    private BigDecimal estimatedDistance; // Maps to totalDistance
	    
	    @Min(value = 1, message = "Duration must be at least 1 minute")
	    private Integer estimatedDuration; // Maps to estimatedDuration
	    
	    // ===== PAYMENT STATUS =====
	    @Pattern(regexp = "^(PENDING|PAID|FAILED|REFUNDED)$", message = "Invalid payment status")
	    private String paymentStatus = "PENDING"; // Maps to payment.status
	    
	    @Size(max = 100, message = "Razorpay order ID cannot exceed 100 characters")
	    private String razorpayOrderId; // Maps to payment.transactionId
	    
	    @Size(max = 100, message = "Razorpay payment ID cannot exceed 100 characters")
	    private String razorpayPaymentId;
	    
	    @Size(max = 255, message = "Razorpay signature cannot exceed 255 characters")
	    private String razorpaySignature;

		public String getPickupLocation() {
			return pickupLocation;
		}

		public void setPickupLocation(String pickupLocation) {
			this.pickupLocation = pickupLocation;
		}

		public String getDropoffLocation() {
			return dropoffLocation;
		}

		public void setDropoffLocation(String dropoffLocation) {
			this.dropoffLocation = dropoffLocation;
		}

		public LocalDate getPickupDate() {
			return pickupDate;
		}

		public void setPickupDate(LocalDate pickupDate) {
			this.pickupDate = pickupDate;
		}

		public LocalTime getPickupTime() {
			return pickupTime;
		}

		public void setPickupTime(LocalTime pickupTime) {
			this.pickupTime = pickupTime;
		}

		public String getGoodsType() {
			return goodsType;
		}

		public void setGoodsType(String goodsType) {
			this.goodsType = goodsType;
		}

		public BigDecimal getGoodsWeight() {
			return goodsWeight;
		}

		public void setGoodsWeight(BigDecimal goodsWeight) {
			this.goodsWeight = goodsWeight;
		}

		public String getGoodsDescription() {
			return goodsDescription;
		}

		public void setGoodsDescription(String goodsDescription) {
			this.goodsDescription = goodsDescription;
		}

		public Boolean getSpecialHandling() {
			return specialHandling;
		}

		public void setSpecialHandling(Boolean specialHandling) {
			this.specialHandling = specialHandling;
		}

		public Boolean getFragile() {
			return fragile;
		}

		public void setFragile(Boolean fragile) {
			this.fragile = fragile;
		}

		public Boolean getPerishable() {
			return perishable;
		}

		public void setPerishable(Boolean perishable) {
			this.perishable = perishable;
		}

		public String getSelectedVehicleType() {
			return selectedVehicleType;
		}

		public void setSelectedVehicleType(String selectedVehicleType) {
			this.selectedVehicleType = selectedVehicleType;
		}

		public String getVehicleId() {
			return vehicleId;
		}

		public void setVehicleId(String vehicleId) {
			this.vehicleId = vehicleId;
		}

		public String getVehicleName() {
			return vehicleName;
		}

		public void setVehicleName(String vehicleName) {
			this.vehicleName = vehicleName;
		}

		public Integer getVehicleCapacity() {
			return vehicleCapacity;
		}

		public void setVehicleCapacity(Integer vehicleCapacity) {
			this.vehicleCapacity = vehicleCapacity;
		}

		public String getContactName() {
			return contactName;
		}

		public void setContactName(String contactName) {
			this.contactName = contactName;
		}

		public String getContactPhone() {
			return contactPhone;
		}

		public void setContactPhone(String contactPhone) {
			this.contactPhone = contactPhone;
		}

		public String getContactEmail() {
			return contactEmail;
		}

		public void setContactEmail(String contactEmail) {
			this.contactEmail = contactEmail;
		}

		public String getAdditionalNotes() {
			return additionalNotes;
		}

		public void setAdditionalNotes(String additionalNotes) {
			this.additionalNotes = additionalNotes;
		}

		public String getPaymentMethod() {
			return paymentMethod;
		}

		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}

		public BigDecimal getBasePrice() {
			return basePrice;
		}

		public void setBasePrice(BigDecimal basePrice) {
			this.basePrice = basePrice;
		}

		public BigDecimal getInsuranceCost() {
			return insuranceCost;
		}

		public void setInsuranceCost(BigDecimal insuranceCost) {
			this.insuranceCost = insuranceCost;
		}

		public BigDecimal getTaxAmount() {
			return taxAmount;
		}

		public void setTaxAmount(BigDecimal taxAmount) {
			this.taxAmount = taxAmount;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public BigDecimal getEstimatedDistance() {
			return estimatedDistance;
		}

		public void setEstimatedDistance(BigDecimal estimatedDistance) {
			this.estimatedDistance = estimatedDistance;
		}

		public Integer getEstimatedDuration() {
			return estimatedDuration;
		}

		public void setEstimatedDuration(Integer estimatedDuration) {
			this.estimatedDuration = estimatedDuration;
		}

		public String getPaymentStatus() {
			return paymentStatus;
		}

		public void setPaymentStatus(String paymentStatus) {
			this.paymentStatus = paymentStatus;
		}

		public String getRazorpayOrderId() {
			return razorpayOrderId;
		}

		public void setRazorpayOrderId(String razorpayOrderId) {
			this.razorpayOrderId = razorpayOrderId;
		}

		public String getRazorpayPaymentId() {
			return razorpayPaymentId;
		}

		public void setRazorpayPaymentId(String razorpayPaymentId) {
			this.razorpayPaymentId = razorpayPaymentId;
		}

		public String getRazorpaySignature() {
			return razorpaySignature;
		}

		public void setRazorpaySignature(String razorpaySignature) {
			this.razorpaySignature = razorpaySignature;
		}
	    
	    
	
}
