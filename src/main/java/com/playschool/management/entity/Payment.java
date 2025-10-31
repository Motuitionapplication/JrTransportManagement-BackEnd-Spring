package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String paymentId;

    // --- Relationships ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private VehicleOwner vehicleOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    // --- Fare Breakdown ---
    @NotNull
    private BigDecimal paymentAmount;

    private BigDecimal baseFare;
    private BigDecimal gstAmount;
    private BigDecimal serviceCharge;
    private BigDecimal insuranceCharge;
    private BigDecimal cancellationCharge;

    // --- Payment Method & Status ---
    @NotBlank
    private String paymentMethod; // UPI, Card, Wallet, etc.

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.COMPLETED ;

    @Column(unique = true)
    private String transactionId;

    private Boolean walletUsed = false;
    private BigDecimal walletAmount = BigDecimal.ZERO;

    private Boolean paidToDriver = false;

    private LocalDateTime releaseDatetime; // After admin approval

    private String referenceId; // Optional booking/consignment ID
    private String remarks;     // Optional admin note / customer note

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public VehicleOwner getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(VehicleOwner vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getInsuranceCharge() {
        return insuranceCharge;
    }

    public void setInsuranceCharge(BigDecimal insuranceCharge) {
        this.insuranceCharge = insuranceCharge;
    }

    public BigDecimal getCancellationCharge() {
        return cancellationCharge;
    }

    public void setCancellationCharge(BigDecimal cancellationCharge) {
        this.cancellationCharge = cancellationCharge;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Boolean getWalletUsed() {
        return walletUsed;
    }

    public void setWalletUsed(Boolean walletUsed) {
        this.walletUsed = walletUsed;
    }

    public BigDecimal getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(BigDecimal walletAmount) {
        this.walletAmount = walletAmount;
    }

    public Boolean getPaidToDriver() {
        return paidToDriver;
    }

    public void setPaidToDriver(Boolean paidToDriver) {
        this.paidToDriver = paidToDriver;
    }

    public LocalDateTime getReleaseDatetime() {
        return releaseDatetime;
    }

    public void setReleaseDatetime(LocalDateTime releaseDatetime) {
        this.releaseDatetime = releaseDatetime;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, CANCELLED, ON_HOLD
    }
}
