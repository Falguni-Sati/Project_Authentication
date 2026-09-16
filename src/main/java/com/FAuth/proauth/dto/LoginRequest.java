package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Schema(
            description = "Registered email address",
            example = "falguni@example.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Account password",
            example = "StrongPass@123"
    )
    @NotBlank
    private String password;
}