package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {

    @Schema(
            description = "Indicates whether the operation was successful",
            example = "true"
    )
    private boolean success;

    @Schema(
            description = "Message describing the result of the operation",
            example = "User Registered Successfully."
    )
    private String message;

    @Schema(
            description = "JWT token returned when applicable",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String token;
}