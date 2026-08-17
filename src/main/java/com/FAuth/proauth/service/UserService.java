package com.FAuth.proauth.service;

import com.FAuth.proauth.dto.ApiResponse;
import com.FAuth.proauth.dto.LoginRequest;
import com.FAuth.proauth.dto.RegisterRequest;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.exception.UserAlreadyExistsException;
import com.FAuth.proauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.FAuth.proauth.entity.Role.USER;
import static com.FAuth.proauth.entity.UserStatus.ACTIVE;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //SaveUser
    public ApiResponse saveUser(RegisterRequest request){
        String email = request.getEmail();
        if(userRepository.existsByEmail(email)){
            throw new UserAlreadyExistsException("Email Already Exists");
        }
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            User user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(encodedPassword)
                    .role(USER)
                    .status(ACTIVE)
                    .emailVerified(false)
                    .build();
            userRepository.save(user);
            return new ApiResponse(true,"User Registered Successfully.");
    }

    //Login
    public ApiResponse login(LoginRequest request){
        Optional<User> user=userRepository.findByEmail(request.getEmail());
        if(user.isEmpty()){
            return new ApiResponse(false,"User not found.");
        }
        boolean matches=passwordEncoder.matches(request.getPassword(),user.get().getPassword());
        if(!matches){
            return new ApiResponse(false,"Password or UserEmail is Invalid.");
        }
        return new ApiResponse(true,"User Logged In Successfully.");
        }
}
