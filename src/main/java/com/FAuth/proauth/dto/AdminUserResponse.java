package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "User information visible to administrators")
public class AdminUserResponse {

    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @Schema(description = "Email address of the user", example = "john@example.com")
    private String email;

    @Schema(description = "Role assigned to the user", example = "USER")
    private String role;

    @Schema(description = "Current account status", example = "ACTIVE")
    private String status;

    @Schema(description = "Whether the user's email has been verified", example = "false")
    private Boolean emailVerified;
}