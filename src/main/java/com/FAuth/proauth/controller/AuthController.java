package com.FAuth.proauth.controller;

import com.FAuth.proauth.dto.ApiResponse;
import com.FAuth.proauth.dto.LoginRequest;
import com.FAuth.proauth.dto.RegisterRequest;
import com.FAuth.proauth.repository.UserRepository;
import com.FAuth.proauth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    //POST(Register)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request){
                ApiResponse response=userService.saveUser(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //POST(Login)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginRequest request){
        ApiResponse response=userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
