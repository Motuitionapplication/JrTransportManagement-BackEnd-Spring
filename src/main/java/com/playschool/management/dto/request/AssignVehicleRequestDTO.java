package com.playschool.management.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AssignVehicleRequestDTO {

	@NotBlank(message = "Vehicle ID cannot be empty")
    private String vehicleId;

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	
	
}
