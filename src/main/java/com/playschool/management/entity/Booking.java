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
}
