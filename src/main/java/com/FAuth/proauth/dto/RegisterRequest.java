package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @Schema(
            description = "Full name of the user",
            example = "Falguni Sati"
    )
    @NotBlank
    private String fullName;

    @Schema(
            description = "Password for the account",
            example = "StrongPass@123"
    )
    @NotBlank
    private String password;

    @Schema(
            description = "Unique email address of the user",
            example = "falguni@example.com"
    )
    @NotBlank
    @Email
    private String email;
}