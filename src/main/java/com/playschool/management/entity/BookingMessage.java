package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "Booking ID is required")
    private String bookingId;
    
    @NotBlank(message = "Sender ID is required")
    private String senderId;
    
    @NotBlank(message = "Sender type is required")
    private String senderType; // CUSTOMER, DRIVER, OWNER
    
    @NotBlank(message = "Message is required")
    @Column(length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;
    
    private String attachmentUrl;
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum MessageType {
        TEXT, IMAGE, DOCUMENT, LOCATION
    }
}
