package com.FAuth.proauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @Schema(
            description = "Refresh token used to obtain a new access token",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String refreshToken;
}