package com.playschool.management.dto.dashboard;

import java.time.LocalDateTime;

public class DriverBookingSummaryDto {

    private String id;
    private String customerName;
    private LocalDateTime pickupTime;
    private String destination;
    private String status;

    public DriverBookingSummaryDto() {
    }

    public DriverBookingSummaryDto(String id, String customerName, LocalDateTime pickupTime,
            String destination, String status) {
        this.id = id;
        this.customerName = customerName;
        this.pickupTime = pickupTime;
        this.destination = destination;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
