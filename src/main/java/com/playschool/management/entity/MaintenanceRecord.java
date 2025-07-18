package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "maintenance_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType type;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", message = "Cost must be positive")
    private BigDecimal cost;
    
    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;
    
    private String serviceCenterId;
    private String receipt; // document URL
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum MaintenanceType {
        ROUTINE_SERVICE, REPAIR, BREAKDOWN, INSPECTION, INSURANCE_CLAIM
    }
}
