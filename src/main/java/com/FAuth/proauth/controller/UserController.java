package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.UserProfileResponse;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

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
