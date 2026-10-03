package com.FAuth.proauth.controller;

import com.FAuth.proauth.entity.Role;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.entity.UserStatus;
import com.FAuth.proauth.repository.UserRepository;
import com.FAuth.proauth.service.JwtService;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void getCurrentUserWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(
                        get("/api/v1/user/me")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUserWithInvalidTokenShouldReturn401() throws Exception {

        mockMvc.perform(
                        get("/api/v1/user/me")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUserWithExpiredTokenShouldReturn401() throws Exception {

        String expiredToken = jwtService.generateExpiredToken("test@example.com");

        mockMvc.perform(
                        get("/api/v1/user/me")
                                .header("Authorization", "Bearer " + expiredToken)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUserWithValidUserTokenShouldReturn200() throws Exception {

        User user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        String token = jwtService.generateToken("test@example.com", 0L);

        mockMvc.perform(
                        get("/api/v1/user/me")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

    @Test
    void userShouldNotAccessAdminDashboard() throws Exception {

        User user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        String token = jwtService.generateToken("test@example.com", 0L);

        mockMvc.perform(
                        get("/api/v1/admin/dashboard")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldAccessAdminDashboard() throws Exception {

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setFullName("Admin User");
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setEmailVerified(false);

        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(admin));

        String token = jwtService.generateToken("admin@example.com",0L);

        mockMvc.perform(
                        get("/api/v1/admin/dashboard")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedUserShouldUpdateProfile() throws Exception {

        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Old Name");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("updated@example.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String token = jwtService.generateToken("user@example.com",0L);

        mockMvc.perform(
                        put("/api/v1/user/me")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "fullName": "Updated Name",
                                "email": "updated@example.com"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void updateProfileWithoutTokenShouldReturn401() throws Exception {

        mockMvc.perform(
                        put("/api/v1/user/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "fullName": "Updated Name",
                                "email": "updated@example.com"
                            }
                            """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfileWithExistingEmailShouldReturn409() throws Exception {

        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Old Name");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("existing@example.com"))
                .thenReturn(true);

        String token = jwtService.generateToken("user@example.com",0L);

        mockMvc.perform(
                        put("/api/v1/user/me")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "fullName": "Updated Name",
                                "email": "existing@example.com"
                            }
                            """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void authenticatedUserShouldChangePassword() throws Exception {

        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Normal User");
        user.setPassword(passwordEncoder.encode("OldPassword123"));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String token = jwtService.generateToken("user@example.com",0L);

        mockMvc.perform(
                        patch("/api/v1/user/password")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "currentPassword": "OldPassword123",
                                "newPassword": "NewPassword123"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Password changed successfully"));
    }

    @Test
    void wrongCurrentPasswordShouldReturn401() throws Exception {

        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Normal User");
        user.setPassword(passwordEncoder.encode("CorrectPassword123"));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        String token = jwtService.generateToken("user@example.com",0L);

        mockMvc.perform(
                        patch("/api/v1/user/password")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "currentPassword": "WrongPassword123",
                                "newPassword": "NewPassword123"
                            }
                            """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePasswordWithoutTokenShouldReturn401() throws Exception {

        mockMvc.perform(
                        patch("/api/v1/user/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "currentPassword": "OldPassword123",
                                "newPassword": "NewPassword123"
                            }
                            """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidProfileUpdateShouldReturn400() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Normal User");
        user.setPassword(passwordEncoder.encode("Password123"));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        String token = jwtService.generateToken("user@example.com",0L);

        mockMvc.perform(
                        put("/api/v1/user/me")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "fullName": "",
                                "email": "invalid-email"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidPasswordChangeShouldReturn400() throws Exception {

        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Normal User");
        user.setPassword(passwordEncoder.encode("Password123"));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        String token = jwtService.generateToken("user@example.com",0L);

        mockMvc.perform(
                        patch("/api/v1/user/password")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "currentPassword": "",
                                "newPassword": "123"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void oldTokenShouldBeRejectedAfterPasswordChange() throws Exception {

        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Normal User");
        user.setPassword(passwordEncoder.encode("OldPassword123"));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setTokenVersion(0L);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Token issued before password change
        String oldToken =
                jwtService.generateToken("user@example.com", 0L);

        // Change password
        mockMvc.perform(
                        patch("/api/v1/user/password")
                                .header("Authorization", "Bearer " + oldToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "currentPassword": "OldPassword123",
                                "newPassword": "NewPassword123"
                            }
                            """)
                )
                .andExpect(status().isOk());

        // Password change should increment tokenVersion
        // 0 → 1

        mockMvc.perform(
                        get("/api/v1/user/me")
                                .header("Authorization", "Bearer " + oldToken)
                )
                .andExpect(status().isUnauthorized());
    }
}