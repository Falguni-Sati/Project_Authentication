package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.*;
import com.FAuth.proauth.entity.RefreshToken;
import com.FAuth.proauth.repository.UserRepository;
import com.FAuth.proauth.service.JwtService;
import com.FAuth.proauth.service.RefreshTokenService;
import com.FAuth.proauth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    //POST(Register)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request){
                ApiResponse response=userService.saveUser(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //POST(Login)
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request){
        Object response=userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken oldRefreshToken =
                refreshTokenService.findByToken(request.getRefreshToken());

        String email = oldRefreshToken.getEmail();

        // Delete old refresh token
        refreshTokenService.deleteByToken(oldRefreshToken.getToken());

        // Generate new access token
        String accessToken = jwtService.generateToken(email);

        // Generate new refresh token
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(email);

        return ResponseEntity.ok(
                new LoginResponse(
                        accessToken,
                        newRefreshToken.getToken()
                )
        );
    }


    //Logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestBody RefreshTokenRequest request) {

        refreshTokenService.deleteByToken(request.getRefreshToken());

        return ResponseEntity.ok(
                new ApiResponse(true, "User Logged Out Successfully.",null)
        );
    }
}
