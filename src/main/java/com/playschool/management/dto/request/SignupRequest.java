package com.playschool.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Signup request containing user registration details.")
public class SignupRequest {

    @Schema(description = "Unique username of the user", example = "john_doe", required = true)
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Schema(description = "User's password (min 6 characters)", example = "securePassword123", required = true)
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @Schema(description = "User's first name", example = "John", required = true)
    @NotBlank
    @Size(max = 50)
    private String firstName;

    @Schema(description = "User's last name", example = "Doe", required = true)
    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Schema(description = "User's phone number", example = "+1234567890")
    @Size(max = 15)
    private String phoneNumber;

    @Schema(description = "Set of roles assigned to the user", example = "[\"ROLE_OWNER\"]")
    private Set<String> role;

    // Constructors
    public SignupRequest() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Set<String> getRole() { return role; }
    public void setRole(Set<String> role) { this.role = role; }
    
    @Schema(description = "Vehicle number for driver/owner registration", example = "MH12AB1234")
    @Size(max = 20)
    private String vehicleNumber;
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
}
