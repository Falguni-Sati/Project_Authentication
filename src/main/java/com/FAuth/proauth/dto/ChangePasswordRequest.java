package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request to change the authenticated user's password")
public class ChangePasswordRequest {

    @NotBlank
    @Schema(
            description = "Current password of the user",
            example = "OldPassword123"
    )
    private String currentPassword;

    @NotBlank
    @Size(min = 8)
    @Schema(
            description = "New password of the user",
            example = "NewPassword123"
    )
    private String newPassword;
}