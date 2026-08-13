package com.FAuth.proauth.service;

import com.FAuth.proauth.dto.RegisterRequest;
import com.FAuth.proauth.entity.User;
import com.FAuth.proauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.FAuth.proauth.entity.Role.USER;
import static com.FAuth.proauth.entity.UserStatus.ACTIVE;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    //SaveUser
    public void saveUser(RegisterRequest request){
        String email=request.getEmail();
        if(userRepository.existsByEmail(email)){
            System.out.println("Email Already Exists");
        }else{
            User user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role(USER)
                    .status(ACTIVE)
                    .emailVerified(false)
                    .build();
            userRepository.save(user);
        }
    }
}
