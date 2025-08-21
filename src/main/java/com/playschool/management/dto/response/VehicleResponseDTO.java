package com.playschool.management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.playschool.management.entity.Vehicle;
import com.playschool.management.entity.Vehicle.VehicleStatus;

public class VehicleResponseDTO {
	
	 private final String id;
    private final String vehicleNumber;
    private final String manufacturer;
    private final String model;
    private final String vehicleType;
    private final BigDecimal capacity; // <-- Changed to final
    private final Vehicle.VehicleStatus status;
    private final LocalDate insuranceExpiryDate;
    private final String driverId;

    
    
    	
	public VehicleResponseDTO(String id, String vehicleNumber, String manufacturer, String model, String vehicleType,
			BigDecimal capacity, VehicleStatus status, LocalDate insuranceExpiryDate, String driverId) {
		super();
		this.id = id;
		this.vehicleNumber = vehicleNumber;
		this.manufacturer = manufacturer;
		this.model = model;
		this.vehicleType = vehicleType;
		this.capacity = capacity;
		this.status = status;
		this.insuranceExpiryDate = insuranceExpiryDate;
		this.driverId = driverId;
	}
	// Getters only, no setter for capacity
    public String getVehicleNumber() { return vehicleNumber; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public String getVehicleType() { return vehicleType; }
    public BigDecimal getCapacity() { return capacity; }
    public Vehicle.VehicleStatus getStatus() { return status; }
    public LocalDate getInsuranceExpiryDate() { return insuranceExpiryDate; }
    public String getDriverId() { return driverId; }
    public String getId() { return id; }
    
}