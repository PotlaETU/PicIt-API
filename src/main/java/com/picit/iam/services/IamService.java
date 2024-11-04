package com.picit.iam.services;

import com.picit.iam.dto.SignUpRequest;
import com.picit.iam.mapper.UserMapper;
import com.picit.iam.model.Settings;
import com.picit.iam.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IamService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

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
}
