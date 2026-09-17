package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Profile information of the authenticated user")
public class UserProfileResponse {

    @Schema(
            description = "Full name of the user",
            example = "Falguni Sati"
    )
    private String fullName;

    @Schema(
            description = "Email address of the user",
            example = "falguni@example.com"
    )
    private String email;

    @Schema(
            description = "Role assigned to the user",
            example = "USER"
    )
    private String role;

    @Schema(
            description = "Current account status",
            example = "ACTIVE"
    )
    private String status;

    @Schema(
            description = "Indicates whether the user's email address has been verified",
            example = "false"
    )
    private Boolean emailVerified;
}