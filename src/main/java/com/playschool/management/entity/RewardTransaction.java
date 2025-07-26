package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "User type is required")
    private String userType; // CUSTOMER, DRIVER, OWNER
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType type;
    
    @NotNull(message = "Points is required")
    private Integer points;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String referenceId; // booking ID, etc.
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum RewardType {
        EARNED, REDEEMED, EXPIRED, BONUS
    }
}
