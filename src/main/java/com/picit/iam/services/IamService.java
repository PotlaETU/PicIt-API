package com.picit.iam.services;

import com.picit.iam.auth.JwtUtil;
import com.picit.iam.dto.LoginRequest;
import com.picit.iam.dto.SignUpRequest;
import com.picit.iam.mapper.UserMapper;
import com.picit.iam.model.User;
import com.picit.iam.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IamService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.username())) {
            throw new RuntimeException("Username already exists");
        }

        var user = userMapper.toUser(signUpRequest);
        user.getSettings().setNotifications(signUpRequest.notifications());
        if (signUpRequest.privacy()) {
            user.getSettings().setPrivacy("public");
        } else {
            user.getSettings().setPrivacy("private");
        }

        //TODO: hash password and authenticate user
        userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        return jwtUtil.generateToken((User) authentication.getPrincipal());
    }
}
