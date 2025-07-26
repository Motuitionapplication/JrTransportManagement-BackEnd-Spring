package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Booking ID is required")
    private String bookingId;
    
    private String driverId;
    private String vehicleOwnerId;
    private String vehicleId;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer overallRating;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer driverRating;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer vehicleRating;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer serviceRating;
    
    @Column(length = 1000)
    private String comment;
    
    private String improvements; // suggestions for improvement
    
    @Column(nullable = false)
    private Boolean wouldRecommend = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
