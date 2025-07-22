package com.playschool.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Supporting entities file for shared embedded classes and enums
 * All standalone entities have been moved to their own files to avoid conflicts
 */
public class SupportingEntities {
    
    // Shared embedded classes for reuse across entities
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country = "India";
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private String phone;
        private String alternatePhone;
        private String email;
    }
    
    // Shared enums
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED
    }
    
    public enum PaymentMethod {
        CARD, UPI, NET_BANKING, WALLET, CASH
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
    
    public enum TransactionType {
        CREDIT, DEBIT, RESERVED, RELEASED
    }
}
