package com.playschool.management.dto.request;

import jakarta.validation.constraints.NotBlank;


public class PasswordUpdateDto {

	 @NotBlank(message = "Old password is required")
	    private String oldPassword;

	    @NotBlank(message = "New password is required")
	    private String newPassword;

		public PasswordUpdateDto(@NotBlank(message = "Old password is required") String oldPassword,
				@NotBlank(message = "New password is required") String newPassword) {
			super();
			this.oldPassword = oldPassword;
			this.newPassword = newPassword;
		}

		public String getOldPassword() {
			return oldPassword;
		}

		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}

		public String getNewPassword() {
			return newPassword;
		}

		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}
	    
}
