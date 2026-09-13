package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.RefreshTokenRequest;
import com.FAuth.proauth.entity.RefreshToken;
import com.FAuth.proauth.service.JwtService;
import com.FAuth.proauth.service.RefreshTokenService;
import com.FAuth.proauth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldRefreshTokenSuccessfully() {

        String oldToken = "old-refresh-token";
        String email = "test@gmail.com";

        RefreshToken oldRefreshToken = RefreshToken.builder()
                .token(oldToken)
                .email(email)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token("new-refresh-token")
                .email(email)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(oldToken);

        when(refreshTokenService.findByToken(oldToken))
                .thenReturn(oldRefreshToken);

        when(jwtService.generateToken(email))
                .thenReturn("new-access-token");

        when(refreshTokenService.createRefreshToken(email))
                .thenReturn(newRefreshToken);

        var response = authController.refreshToken(request);

        assertEquals(200, response.getStatusCode().value());

        verify(refreshTokenService).findByToken(oldToken);
        verify(refreshTokenService).deleteByToken(oldToken);
        verify(jwtService).generateToken(email);
        verify(refreshTokenService).createRefreshToken(email);

        InOrder inOrder = inOrder(
                refreshTokenService,
                jwtService
        );

        inOrder.verify(refreshTokenService).findByToken(oldToken);
        inOrder.verify(refreshTokenService).deleteByToken(oldToken);
        inOrder.verify(jwtService).generateToken(email);
        inOrder.verify(refreshTokenService).createRefreshToken(email);
    }

    @Test
    void shouldLogoutSuccessfully() {

        String refreshToken = "refresh-token-123";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        var response = authController.logout(request);

        assertEquals(200, response.getStatusCode().value());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "User Logged Out Successfully.",
                response.getBody().getMessage()
        );

        verify(refreshTokenService).deleteByToken(refreshToken);
    }
}