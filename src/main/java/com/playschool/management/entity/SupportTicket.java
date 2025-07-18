package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Description is required")
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;
    
    private String assignedToId;
    private String category;
    private String subcategory;
    
    private String resolution;
    private LocalDateTime resolvedAt;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum TicketType {
        TECHNICAL_ISSUE, ACCOUNT_HELP, BILLING_INQUIRY, 
        FEATURE_REQUEST, GENERAL_INQUIRY, BUG_REPORT
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED, ESCALATED
    }
}
