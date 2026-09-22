package com.FAuth.proauth.controller;

import com.FAuth.proauth.entity.Role;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.entity.UserStatus;
import com.FAuth.proauth.repository.UserRepository;
import com.FAuth.proauth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

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

        String token = jwtService.generateToken("test@example.com");

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

        String token = jwtService.generateToken("test@example.com");

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

        String token = jwtService.generateToken("admin@example.com");

        mockMvc.perform(
                        get("/api/v1/admin/dashboard")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }
}