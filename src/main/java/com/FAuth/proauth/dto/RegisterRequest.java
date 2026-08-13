package com.FAuth.proauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String password;
    @NotBlank
    @Email
    private String email;
}
