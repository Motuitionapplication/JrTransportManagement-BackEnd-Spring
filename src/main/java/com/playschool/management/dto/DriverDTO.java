package com.playschool.management.dto;

public class DriverDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status;
    private String assignedVehicleInfo;

    

    public DriverDTO(String id, String firstName, String lastName, String email, String phoneNumber, String status,
			String assignedVehicleInfo) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.status = status;
		this.assignedVehicleInfo = assignedVehicleInfo;
	}
	public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
	public String getAssignedVehicleInfo() {
		return assignedVehicleInfo;
	}
	public void setAssignedVehicleInfo(String assignedVehicleInfo) {
		this.assignedVehicleInfo = assignedVehicleInfo;
	}
    
}
