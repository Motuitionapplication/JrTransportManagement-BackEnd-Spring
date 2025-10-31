package com.playschool.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Lightweight projection of the authenticated driver's profile for navbar display.
 */
@Schema(name = "DriverProfileSummary", description = "Minimal driver profile details for the logged-in user")
public class DriverProfileSummaryDto {

    @Schema(description = "Driver record identifier", example = "a2d4fa25-3fbb-4a93-940e-73413797fbea")
    private String id;

    @Schema(description = "Linked authentication user identifier", example = "42")
    private String userId;

    @Schema(description = "Driver's first name", example = "Asha")
    private String firstName;

    @Schema(description = "Driver's last name", example = "Sharma")
    private String lastName;

    @Schema(description = "Primary application role", example = "ROLE_DRIVER")
    private String role;

    @Schema(description = "Driver contact email", example = "asha.sharma@example.com")
    private String email;

    @Schema(description = "Public avatar URL (base64 data URI or CDN path)")
    private String avatarUrl;

    public DriverProfileSummaryDto() {
    }

    public DriverProfileSummaryDto(String id, String userId, String firstName, String lastName, String role, String email, String avatarUrl) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
