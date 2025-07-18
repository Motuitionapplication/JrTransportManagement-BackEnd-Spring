package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "Owner ID is required")
    private String ownerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String referenceId; // booking ID, payment ID, etc.
    private String transactionId; // unique transaction identifier
    private String transactionType; // for compatibility with service
    private LocalDateTime transactionDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.COMPLETED;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum TransactionType {
        CREDIT, DEBIT, REFUND, PENALTY
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
    
    // Explicit getters and setters for compatibility
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public void setBalanceAfter(BigDecimal balanceAfter) {
        // This could be added as a field if needed for service compatibility
        // For now, we'll just accept the setter call
    }
}
