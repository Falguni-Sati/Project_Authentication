package com.FAuth.proauth.service;

import com.FAuth.proauth.dto.RegisterRequest;
import com.FAuth.proauth.exception.RefreshTokenException;
import com.FAuth.proauth.exception.UserAlreadyExistsException;
import com.FAuth.proauth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import com.FAuth.proauth.exception.InvalidCredentialsException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.FAuth.proauth.entity.User;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import com.FAuth.proauth.dto.LoginRequest;
import com.FAuth.proauth.dto.LoginResponse;
import com.FAuth.proauth.entity.RefreshToken;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.saveUser(request);

        assertTrue(response.isSuccess());
        assertEquals("User Registered Successfully.", response.getMessage());

        verify(userRepository).existsByEmail("test@gmail.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.saveUser(request)
        );

        verify(userRepository).existsByEmail("test@gmail.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-123");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("test@gmail.com"))
                .thenReturn("access-token-123");

        when(refreshTokenService.createRefreshToken("test@gmail.com"))
                .thenReturn(refreshToken);

        LoginResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("access-token-123", response.getAccessToken());
        assertEquals("refresh-token-123", response.getRefreshToken());

        verify(userRepository).findByEmail("test@gmail.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateToken("test@gmail.com");
        verify(refreshTokenService).createRefreshToken("test@gmail.com");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(userRepository).findByEmail("test@gmail.com");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");

        verify(jwtService, never()).generateToken(anyString());
        verify(refreshTokenService, never()).createRefreshToken(anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@gmail.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(userRepository).findByEmail("unknown@gmail.com");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(anyString());

        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsExpired() {

        String refreshToken = "expired-refresh-token";

        when(refreshTokenService.findByToken(refreshToken))
                .thenThrow(
                        new RefreshTokenException("Refresh token has expired")
                );

        assertThrows(
                RefreshTokenException.class,
                () -> refreshTokenService.findByToken(refreshToken)
        );

        verify(refreshTokenService).findByToken(refreshToken);
    }
}