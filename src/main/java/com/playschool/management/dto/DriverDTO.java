package com.playschool.management.dto;

public class DriverDTO {

	
	 private String Id;
	    private String firstName;
	    private String lastName;
	    private String email;
	    private String phoneNumber;
	    private String status;
		public DriverDTO(String id, String firstName, String lastName, String email, String phoneNumber,
				String status) {
			super();
			this.Id = Id;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.phoneNumber = phoneNumber;
			this.status = status;
		}
		public String getId() {
			return Id;
		}
		public void setId(String Id) {
			this.Id = Id;
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
	    
	    
}
