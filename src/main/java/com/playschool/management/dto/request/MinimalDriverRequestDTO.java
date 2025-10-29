package com.playschool.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * COPILOT-FIX: DTO used when frontend auto-creates a placeholder driver profile.
 */
public class MinimalDriverRequestDTO {

    @NotBlank(message = "User identifier is required to create a driver profile")
    private String userId;

    private String firstName;

    private String lastName;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String phoneNumber;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
