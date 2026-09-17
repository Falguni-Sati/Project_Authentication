package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.*;
import com.FAuth.proauth.entity.RefreshToken;
import com.FAuth.proauth.service.JwtService;
import com.FAuth.proauth.service.RefreshTokenService;
import com.FAuth.proauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Authentication",
        description = "APIs for user registration, login, token refresh and logout"
)
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    // POST(Register)
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with an encrypted password"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        ApiResponse<Void> response = userService.saveUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // POST(Login)
    @Operation(
            summary = "Login user",
            description = "Authenticates the user and returns an access token and refresh token"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/loginUser")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(userService.login(request));
    }

    // Refresh Token
    @Operation(
            summary = "Refresh access token",
            description = "Validates the refresh token, rotates it, and returns a new access token and refresh token"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tokens refreshed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is invalid or expired",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken oldRefreshToken =
                refreshTokenService.findByToken(request.getRefreshToken());

        String email = oldRefreshToken.getEmail();

        refreshTokenService.deleteByToken(oldRefreshToken.getToken());

        String accessToken = jwtService.generateToken(email);

        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(email);

        return ResponseEntity.ok(
                new LoginResponse(
                        accessToken,
                        newRefreshToken.getToken()
                )
        );
    }

    // Logout
    @Operation(
            summary = "Logout user",
            description = "Invalidates the provided refresh token and logs the user out"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User logged out successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestBody RefreshTokenRequest request) {

        refreshTokenService.deleteByToken(request.getRefreshToken());

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "User Logged Out Successfully.",
                        null
                )
        );
    }
}