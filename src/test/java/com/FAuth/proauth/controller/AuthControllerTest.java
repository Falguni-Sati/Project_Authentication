package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.RefreshTokenRequest;
import com.FAuth.proauth.entity.RefreshToken;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.exception.RefreshTokenException;
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

        User user = new User();
        user.setEmail(email);
        user.setTokenVersion(0L);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(oldToken);

        when(refreshTokenService.findByToken(oldToken))
                .thenReturn(oldRefreshToken);

        when(userService.getUserByEmail(email))
                .thenReturn(user);

        when(jwtService.generateToken(email, 0L))
                .thenReturn("new-access-token");

        when(refreshTokenService.createRefreshToken(email))
                .thenReturn(newRefreshToken);

        var response = authController.refreshToken(request);

        assertEquals(200, response.getStatusCode().value());

        verify(refreshTokenService).findByToken(oldToken);
        verify(userService).getUserByEmail(email);
        verify(refreshTokenService).deleteByToken(oldToken);
        verify(jwtService).generateToken(email, 0L);
        verify(refreshTokenService).createRefreshToken(email);

        InOrder inOrder = inOrder(
                refreshTokenService,
                userService,
                jwtService
        );

        inOrder.verify(refreshTokenService).findByToken(oldToken);
        inOrder.verify(userService).getUserByEmail(email);
        inOrder.verify(refreshTokenService).deleteByToken(oldToken);
        inOrder.verify(jwtService).generateToken(email, 0L);
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

    @Test
    void shouldRejectInvalidRefreshToken() {

        String invalidToken = "invalid-refresh-token";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(invalidToken);

        when(refreshTokenService.findByToken(invalidToken))
                .thenThrow(new RefreshTokenException("Refresh token not found"));

        assertThrows(
                RefreshTokenException.class,
                () -> authController.refreshToken(request)
        );

        verify(refreshTokenService).findByToken(invalidToken);

        verify(refreshTokenService, never())
                .deleteByToken(anyString());

        verify(jwtService, never())
                .generateToken(anyString(),anyLong());

        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }

    @Test
    void shouldRejectExpiredRefreshToken() {

        String expiredToken = "expired-refresh-token";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(expiredToken);

        when(refreshTokenService.findByToken(expiredToken))
                .thenThrow(new RefreshTokenException("Refresh token has expired"));

        assertThrows(
                RefreshTokenException.class,
                () -> authController.refreshToken(request)
        );

        verify(refreshTokenService).findByToken(expiredToken);

        verify(refreshTokenService, never())
                .deleteByToken(anyString());

        verify(jwtService, never())
                .generateToken(anyString(),anyLong());

        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }

    @Test
    void shouldRejectReusedRefreshToken() {

        String reusedToken = "old-refresh-token";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(reusedToken);

        when(refreshTokenService.findByToken(reusedToken))
                .thenThrow(new RefreshTokenException("Refresh token not found"));

        assertThrows(
                RefreshTokenException.class,
                () -> authController.refreshToken(request)
        );

        verify(refreshTokenService).findByToken(reusedToken);

        verify(jwtService, never())
                .generateToken(anyString(),anyLong());

        verify(refreshTokenService, never())
                .createRefreshToken(anyString());

        verify(refreshTokenService, never())
                .deleteByToken(anyString());
    }

    @Test
    void shouldInvalidateRefreshTokenAfterLogout() {

        String refreshToken = "logout-token";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        // Logout
        authController.logout(request);

        verify(refreshTokenService)
                .deleteByToken(refreshToken);

        // Trying to use the same token afterwards
        when(refreshTokenService.findByToken(refreshToken))
                .thenThrow(new RefreshTokenException("Refresh token not found"));

        assertThrows(
                RefreshTokenException.class,
                () -> authController.refreshToken(request)
        );

        verify(jwtService, never())
                .generateToken(anyString(),anyLong());

        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }
}