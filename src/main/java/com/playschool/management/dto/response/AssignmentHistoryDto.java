package com.playschool.management.dto.response;

import java.time.LocalDate;

public class AssignmentHistoryDto {

	 private String vehicleNumber;
	    private String vehicleModel;
	    private String driverFirstName;
	    private String driverLastName;
	    private String driverPhoneNumber;
	    private LocalDate assignmentStartDate;
	    private LocalDate assignmentEndDate;
		public AssignmentHistoryDto(String vehicleNumber, String vehicleModel, String driverFirstName,
				String driverLastName, String driverPhoneNumber, LocalDate assignmentStartDate,
				LocalDate assignmentEndDate) {
			super();
			this.vehicleNumber = vehicleNumber;
			this.vehicleModel = vehicleModel;
			this.driverFirstName = driverFirstName;
			this.driverLastName = driverLastName;
			this.driverPhoneNumber = driverPhoneNumber;
			this.assignmentStartDate = assignmentStartDate;
			this.assignmentEndDate = assignmentEndDate;
		}
		public String getVehicleNumber() {
			return vehicleNumber;
		}
		public void setVehicleNumber(String vehicleNumber) {
			this.vehicleNumber = vehicleNumber;
		}
		public String getVehicleModel() {
			return vehicleModel;
		}
		public void setVehicleModel(String vehicleModel) {
			this.vehicleModel = vehicleModel;
		}
		public String getDriverFirstName() {
			return driverFirstName;
		}
		public void setDriverFirstName(String driverFirstName) {
			this.driverFirstName = driverFirstName;
		}
		public String getDriverLastName() {
			return driverLastName;
		}
		public void setDriverLastName(String driverLastName) {
			this.driverLastName = driverLastName;
		}
		public String getDriverPhoneNumber() {
			return driverPhoneNumber;
		}
		public void setDriverPhoneNumber(String driverPhoneNumber) {
			this.driverPhoneNumber = driverPhoneNumber;
		}
		public LocalDate getAssignmentStartDate() {
			return assignmentStartDate;
		}
		public void setAssignmentStartDate(LocalDate assignmentStartDate) {
			this.assignmentStartDate = assignmentStartDate;
		}
		public LocalDate getAssignmentEndDate() {
			return assignmentEndDate;
		}
		public void setAssignmentEndDate(LocalDate assignmentEndDate) {
			this.assignmentEndDate = assignmentEndDate;
		}
		
		
	    
	    
}
