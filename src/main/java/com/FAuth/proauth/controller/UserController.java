package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.UserProfileResponse;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile information of the currently authenticated user"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or JWT token is invalid",
                    content = @Content(
                            schema = @Schema(
                                    implementation = com.FAuth.proauth.dto.ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        UserProfileResponse response = new UserProfileResponse(
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getEmailVerified()
        );

        return ResponseEntity.ok(response);
    }
}