package com.FAuth.proauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Admin",
        description = "APIs accessible only to users with ADMIN role"
)
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Operation(
            summary = "Access admin dashboard",
            description = "Returns the admin dashboard message. Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Admin dashboard accessed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or JWT token is invalid",
                    content = @Content(
                            schema = @Schema(
                                    implementation = com.FAuth.proauth.dto.SecurityErrorResponse.class
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied. ADMIN role required.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = com.FAuth.proauth.dto.SecurityErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Admin");
    }
}