package com.picit.iam.services;

import com.picit.iam.auth.JwtUtil;
import com.picit.iam.dto.*;
import com.picit.iam.entity.Settings;
import com.picit.iam.entity.User;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.mapper.UserMapper;
import com.picit.iam.repository.UserProfileRepository;
import com.picit.iam.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final UserProfileRepository userProfileRepository;

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
                .token(
                        TokenResponse.builder()
                                .token(token)
                                .refreshToken(user.getRefreshToken())
                                .build()
                )
                .expiration(Date.from(new Date()
                                .toInstant()
                                .plusMillis(jwtUtil.getExpirationTime()))
                        .toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        logger.info("Login request received for username: {}", loginRequest.username());

        User authUser = userRepository.findByUsername(loginRequest.username()).orElseThrow(
                () -> new UserNotFound("User not found")
        );
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
                .token(
                        TokenResponse.builder()
                                .token(token)
                                .refreshToken(refreshToken)
                                .build()
                )
                .expiration(Date.from(new Date()
                                .toInstant()
                                .plusMillis(jwtUtil.getExpirationTime()))
                        .toString())
                .email(authUser.getEmail())
                .username(authUser.getUsername())
                .build();
        logger.info("Login response generated for username: {}", loginRequest.username());
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<TokenResponse> refresh(TokenRefreshRequest tokenRefreshRequest) {
        String refreshToken = tokenRefreshRequest.refreshToken();
        String username = jwtUtil.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFound("User not found")
        );
        if (!refreshToken.equals(user.getRefreshToken()) || jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        var loginResponse = TokenResponse.builder()
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

        User authUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFound("User not found")
        );

        authUser.setRefreshToken(null);
        userRepository.save(authUser);

        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();

    }

    public ResponseEntity<UserDto> getUser(String userId) {
        var user = userRepository.findByUsername(userId).orElseThrow(
                () -> new UserNotFound("User not found"));
        var userProfile = userProfileRepository.findByUserId(userId);
        return ResponseEntity.ok(userMapper.toUserDto(user, userProfile));
    }
}
