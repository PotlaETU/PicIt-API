package com.picit.iam.services;

import com.picit.iam.auth.JwtUtil;
import com.picit.iam.dto.LoginRequest;
import com.picit.iam.dto.LoginResponse;
import com.picit.iam.dto.SignUpRequest;
import com.picit.iam.dto.TokenRefreshRequest;
import com.picit.iam.mapper.UserMapper;
import com.picit.iam.model.Settings;
import com.picit.iam.model.User;
import com.picit.iam.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Service
@AllArgsConstructor
public class IamService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(IamService.class);

    public ResponseEntity<LoginResponse> signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.username()) || userRepository.existsByEmail(signUpRequest.email())) {
            throw new UsernameNotFoundException("Username or email already exists");
        }

        var user = userMapper.toUser(signUpRequest);
        user.setRefreshToken(jwtUtil.generateRefreshToken(user));
        user.setSettings(Settings.builder()
                .build());
        user.getSettings().setNotifications(signUpRequest.notifications());
        if (signUpRequest.privacy()) {
            user.getSettings().setPrivacy("public");
        } else {
            user.getSettings().setPrivacy("private");
        }
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        userRepository.save(user);
        String token = jwtUtil.generateToken(user);

        var loginResponse = LoginResponse.builder()
                .token(token)
                .expiration(Date.from(new Date()
                                .toInstant()
                                .plusMillis(jwtUtil.getExpirationTime()))
                        .toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .refreshToken(user.getRefreshToken())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        logger.info("Login request received for username: {}", loginRequest.username());

        User authUser = userRepository.findByUsername(loginRequest.username());
        if (authUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        String token = jwtUtil.generateToken(authUser);
        String refreshToken;
        if (jwtUtil.isTokenValid(authUser.getRefreshToken(), authUser)) {
            refreshToken = authUser.getRefreshToken();
        } else {
            refreshToken = jwtUtil.generateRefreshToken(authUser);
            authUser.setRefreshToken(refreshToken);
            userRepository.save(authUser);
        }

        var loginResponse = LoginResponse.builder()
                .token(token)
                .expiration(Date.from(new Date()
                                .toInstant()
                                .plusMillis(jwtUtil.getExpirationTime()))
                        .toString())
                .email(authUser.getEmail())
                .username(authUser.getUsername())
                .refreshToken(refreshToken)
                .build();
        logger.info("Login response generated for username: {}", loginRequest.username());
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<LoginResponse> refresh(TokenRefreshRequest tokenRefreshRequest) {
        String refreshToken = tokenRefreshRequest.refreshToken();
        String username = jwtUtil.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username);
        if (user == null || !refreshToken.equals(user.getRefreshToken()) || jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        var loginResponse = LoginResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        User authUser = userRepository.findByUsername(username);
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        authUser.setRefreshToken(null);
        userRepository.save(authUser);

        return ResponseEntity.ok().build();
    }}
