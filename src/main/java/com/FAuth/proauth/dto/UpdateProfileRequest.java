package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request to update the authenticated user's profile")
public class UpdateProfileRequest {

    @NotBlank
    @Schema(
            description = "Updated full name of the user",
            example = "John Doe"
    )
    private String fullName;

    @NotBlank
    @Email
    @Schema(
            description = "Updated email address of the user",
            example = "john@example.com"
    )
    private String email;
}