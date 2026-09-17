package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Security-related error response")
public class SecurityErrorResponse {

    @Schema(
            description = "Indicates whether the request was successful",
            example = "false"
    )
    private boolean success;

    @Schema(
            description = "Description of the security error",
            example = "Authentication required"
    )
    private String message;

    @Schema(
            description = "Date and time when the error occurred",
            example = "2026-08-24T14:30:00"
    )
    private LocalDateTime timestamp;
}