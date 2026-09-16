package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(
            description = "Indicates whether the request was successful",
            example = "false"
    )
    private boolean success;

    @Schema(
            description = "Description of the error",
            example = "Email Already Exists"
    )
    private String message;

    @Schema(
            description = "Date and time when the error occurred",
            example = "2026-08-23T14:30:00"
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "Field-specific validation errors",
            example = "{\"email\":\"must be a valid email address\"}"
    )
    private Map<String, String> errors;
}