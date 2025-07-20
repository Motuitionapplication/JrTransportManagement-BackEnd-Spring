package com.playschool.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Login request containing username and password.",
    example = "{\n  \"username\": \"john_doe\",\n  \"password\": \"securePassword123\"\n}"
)
public class LoginRequest {
    @Schema(description = "Unique username of the user", example = "john_doe", required = true)
    @NotBlank
    private String username;

    @Schema(description = "User's password", example = "securePassword123", required = true)
    @NotBlank
    private String password;

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
