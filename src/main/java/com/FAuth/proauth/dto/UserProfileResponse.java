package com.FAuth.proauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserProfileResponse {

    private String fullName;
    private String email;
    private String role;
    private String status;
    private Boolean emailVerified;
}
