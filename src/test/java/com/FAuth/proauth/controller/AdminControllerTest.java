package com.FAuth.proauth.controller;

import com.FAuth.proauth.repository.UserRepository;
import com.FAuth.proauth.service.JwtService;
import org.junit.jupiter.api.Test;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.entity.Role;
import com.FAuth.proauth.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageImpl;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void adminUsersShouldLimitPageSizeTo50() throws Exception {

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setFullName("Admin User");
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setEmailVerified(false);

        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(admin));

        when(userRepository.findAll(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);

                    return new PageImpl<>(
                            List.of(),
                            pageable,
                            0
                    );
                });

        String token = jwtService.generateToken("admin@example.com",0L);

        mockMvc.perform(
                        get("/api/v1/admin/users")
                                .param("page", "0")
                                .param("size", "100")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size").value(50));
    }

    @Test
    void userShouldNotAccessAdminUsers() throws Exception {

        User user = new User();
        user.setEmail("user@example.com");
        user.setFullName("Normal User");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        String token = jwtService.generateToken("user@example.com",0L);

        mockMvc.perform(
                        get("/api/v1/admin/users")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserShouldNotAccessAdminUsers() throws Exception {

        mockMvc.perform(
                        get("/api/v1/admin/users")
                )
                .andExpect(status().isUnauthorized());
    }
}