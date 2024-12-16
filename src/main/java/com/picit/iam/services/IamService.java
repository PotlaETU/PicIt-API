package com.picit.iam.services;

import com.picit.iam.auth.JwtUtil;
import com.picit.iam.dto.login.LoginRequest;
import com.picit.iam.dto.login.LoginResponse;
import com.picit.iam.dto.login.SignUpRequest;
import com.picit.iam.dto.responsetype.MessageResponse;
import com.picit.iam.dto.token.TokenRefreshRequest;
import com.picit.iam.dto.token.TokenResponse;
import com.picit.iam.dto.user.UserDto;
import com.picit.iam.entity.Settings;
import com.picit.iam.entity.User;
import com.picit.iam.exceptions.ConflictException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    private static final String USER_NOT_FOUND = "User not found";

    public ResponseEntity<LoginResponse> signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.username()) || userRepository.existsByEmail(signUpRequest.email())) {
            throw new ConflictException("Username or email already exists");
        }

        var user = userMapper.toUser(signUpRequest);
        var refreshToken = jwtUtil.generateRefreshToken(user);
        user.setSettings(Settings.builder()
                .build());
        user.getSettings().setNotifications(signUpRequest.notifications());
        if (signUpRequest.privacy()) {
            user.getSettings().setPrivacy("public");
        } else {
            user.getSettings().setPrivacy("private");
        }
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        var userSaved = userRepository.save(user);

        var userProfile = userProfileRepository.save(userMapper.toUserProfile(userSaved));
        user.setUserProfile(userProfile);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        ResponseCookie jwtCookie = generateCookies(token, refreshToken).getFirst();
        ResponseCookie refreshTokenCookie = generateCookies(token, refreshToken).get(1);
        var loginResponse = setCookiesAndLoginResponse(token, refreshToken, user);

        if (logger.isInfoEnabled()) {
            logger.info("SignUp response generated for username: {}", signUpRequest.username());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString(), refreshTokenCookie.toString())
                .body(loginResponse);
    }

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        User authUser;
        logger.info("Login request received for username: {}", loginRequest.username());
        if (loginRequest.username() == null && loginRequest.email() != null) {
            authUser = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                    () -> new UserNotFound(USER_NOT_FOUND)
            );
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authUser.getUsername(), loginRequest.password())
            );
        } else {
            authUser = userRepository.findByUsername(loginRequest.username()).orElseThrow(
                    () -> new UserNotFound(USER_NOT_FOUND)
            );
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );
        }

        String token = jwtUtil.generateToken(authUser);
        String refreshToken = jwtUtil.generateRefreshToken(authUser);

        ResponseCookie jwtCookie = generateCookies(token, refreshToken).getFirst();
        ResponseCookie refreshTokenCookie = generateCookies(token, refreshToken).get(1);
        var loginResponse = setCookiesAndLoginResponse(token, refreshToken, authUser);

        if (logger.isInfoEnabled()) {
            logger.info("Login response generated for username: {}", loginRequest.username());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString(), refreshTokenCookie.toString())
                .body(loginResponse);
    }

    public ResponseEntity<TokenResponse> refresh(TokenRefreshRequest tokenRefreshRequest, String username, HttpServletRequest http) {
        String refreshToken = tokenRefreshRequest != null ? tokenRefreshRequest.refreshToken() : null;
        if (refreshToken == null) {
            refreshToken = getTokenFromCookie("refreshToken", http);
        }
        String usernameToken = jwtUtil.extractUsername(refreshToken);

        User user = userRepository.findByUsername(usernameToken).orElseThrow(
                () -> new UserNotFound(USER_NOT_FOUND)
        );
        if (jwtUtil.isTokenExpired(refreshToken) || !username.equals(usernameToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);
        userRepository.save(user);

        var loginResponse = TokenResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            token = getTokenFromCookie("jwt", request);
        } else {
            token = authorizationHeader.substring(7);
        }
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String username = jwtUtil.extractUsername(token);

        User authUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFound(USER_NOT_FOUND)
        );

        userRepository.save(authUser);

        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = jwtUtil.getCleanRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshTokenCookie.toString())
                .build();

    }

    public UserDto getUser(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFound(USER_NOT_FOUND));
        var userProfile = userProfileRepository.findByUserId(user.getId());
        return userMapper.toUserDto(user, userProfile);
    }

    private List<ResponseCookie> generateCookies(String token, String refreshToken) {
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(3600L * 24 * 7)
                .sameSite("Strict")
                .build();
        return List.of(jwtCookie, refreshTokenCookie);
    }

    private String getTokenFromCookie(String cookieName, HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private LoginResponse setCookiesAndLoginResponse(String token, String refreshToken, User user) {
        return LoginResponse.builder()
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
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }


    public ResponseEntity<MessageResponse> resetPassword(String oldPassword, String newPassword, String name) {
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new UserNotFound(USER_NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.builder()
                            .message("Old password is incorrect")
                            .timestamp(LocalDateTime.now())
                            .build());
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok()
                .body(MessageResponse.builder()
                        .message("Password changed successfully")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    public ResponseEntity<MessageResponse> validate(String token) {
        if (jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.builder()
                            .message("Token is expired")
                            .timestamp(LocalDateTime.now())
                            .build());
        }
        return ResponseEntity.ok()
                .body(MessageResponse.builder()
                        .message("Token is valid")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
