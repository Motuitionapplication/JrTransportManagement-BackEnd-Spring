package com.playschool.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response wrapper for driver avatar uploads to keep front-end contracts explicit.
 */
@Schema(name = "DriverAvatarResponse", description = "Response returned after uploading a driver profile photo")
public class DriverAvatarResponse {

    @Schema(description = "Updated avatar URL (base64 data URI or external resource path)")
    private String avatarUrl;

    public DriverAvatarResponse() {
    }

    public DriverAvatarResponse(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
