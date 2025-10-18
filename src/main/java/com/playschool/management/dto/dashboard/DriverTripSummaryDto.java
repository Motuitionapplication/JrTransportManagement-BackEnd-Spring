package com.playschool.management.dto.dashboard;

import java.time.LocalDateTime;

public class DriverTripSummaryDto {

    private String id;
    private String pickupLocation;
    private String dropLocation;
    private LocalDateTime scheduledAt;
    private String status;
    private double amount;

    public DriverTripSummaryDto() {
    }

    public DriverTripSummaryDto(String id, String pickupLocation, String dropLocation,
            LocalDateTime scheduledAt, String status, double amount) {
        this.id = id;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
