package com.playschool.management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.playschool.management.entity.Booking.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private String paymentId;
    private String transactionId;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public PaymentResponseDTO(String paymentId, String transactionId, BigDecimal totalAmount,
			PaymentStatus paymentStatus, LocalDateTime createdAt) {
		super();
		this.paymentId = paymentId;
		this.transactionId = transactionId;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
		this.createdAt = createdAt;
	}
    

	
    
    
}
