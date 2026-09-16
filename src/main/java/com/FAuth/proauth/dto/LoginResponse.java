package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    @Schema(
            description = "JWT access token used to authenticate API requests",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String accessToken;

    @Schema(
            description = "Refresh token used to obtain a new access token",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String refreshToken;
}
