package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(
            description = "Indicates whether the operation was successful",
            example = "true"
    )
    private boolean success;

    @Schema(
            description = "Message describing the result",
            example = "User Registered Successfully."
    )
    private String message;

    @Schema(
            description = "Response data",
            nullable = true
    )
    private T data;
}