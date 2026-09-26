package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.ApiResponse;
import com.FAuth.proauth.dto.PageResponse;
import com.FAuth.proauth.dto.SecurityErrorResponse;
import com.FAuth.proauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.FAuth.proauth.dto.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;

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

    @GetMapping("/users")
    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of users. Accessible only to administrators."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = SecurityErrorResponse.class
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            schema = @Schema(
                                    implementation = SecurityErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable
    ) {

        int maxPageSize = 50;

        if (pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    maxPageSize,
                    pageable.getSort()
            );
        }

        PageResponse<AdminUserResponse> pageResponse =
                userService.getAllUsers(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Users retrieved successfully",
                        pageResponse
                )
        );
    }
}