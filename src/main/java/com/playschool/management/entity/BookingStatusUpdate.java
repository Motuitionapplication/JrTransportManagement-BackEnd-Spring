package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_status_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "Booking ID is required")
    private String bookingId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateType updateType;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String updatedById;
    private String updatedByType; // CUSTOMER, DRIVER, OWNER, SYSTEM
    
    private String location;
    private Double latitude;
    private Double longitude;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum UpdateType {
        PICKUP_STARTED, PICKUP_COMPLETED, IN_TRANSIT, DELIVERY_STARTED, 
        DELIVERY_COMPLETED, CANCELLED, DELAYED, RESCHEDULED
    }
}
