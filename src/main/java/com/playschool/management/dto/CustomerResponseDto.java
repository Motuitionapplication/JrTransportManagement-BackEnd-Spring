package com.playschool.management.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CustomerResponseDto {
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String id;
    private Profile profile;
    private String userId;
    private String password;
    // Add other fields as needed

    @Data
    public static class Profile {
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
		public String getFatherName() {
			return fatherName;
		}
		public void setFatherName(String fatherName) {
			this.fatherName = fatherName;
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
		public String getAlternatePhone() {
			return alternatePhone;
		}
		public void setAlternatePhone(String alternatePhone) {
			this.alternatePhone = alternatePhone;
		}
		public Address getAddress() {
			return address;
		}
		public void setAddress(Address address) {
			this.address = address;
		}
		public String getProfilePhoto() {
			return profilePhoto;
		}
		public void setProfilePhoto(String profilePhoto) {
			this.profilePhoto = profilePhoto;
		}
		public LocalDate getDateOfBirth() {
			return dateOfBirth;
		}
		public void setDateOfBirth(LocalDate dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}
		private String firstName;
        private String lastName;
        private String fatherName;
        private String email;
        private String phoneNumber;
        private String alternatePhone;
        private Address address;
        private String profilePhoto;
        private LocalDate dateOfBirth;
    }

    @Data
    public static class Address {
        public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getPincode() {
			return pincode;
		}
		public void setPincode(String pincode) {
			this.pincode = pincode;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		private String street;
        private String city;
        private String state;
        private String pincode;
        private String country;
    }
}