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
import com.picit.iam.entity.points.Points;
import com.picit.iam.exceptions.ConflictException;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.mapper.UserMapper;
import com.picit.iam.repository.UserProfileRepository;
import com.picit.iam.repository.UserRepository;
import com.picit.iam.repository.points.PointsRepository;
import io.jsonwebtoken.JwtException;
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
    private final PointsRepository pointsRepository;

    public ResponseEntity<LoginResponse> signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.username()) || userRepository.existsByEmail(signUpRequest.email())) {
            throw new ConflictException("Username or email already exists");
        }
        var user = userMapper.toUser(signUpRequest);
        user.setUsername(user.getUsername().toLowerCase());
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

        var pointsUser = Points.builder()
                .pointsNb(0)
                .userId(user.getId())
                .build();
        pointsRepository.save(pointsUser);

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
        String s = loginRequest.username() == null ? loginRequest.email() : loginRequest.username();
        logger.info("Login request received for username: {}", s);
        if (loginRequest.username() == null && loginRequest.email() != null) {
            authUser = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                    () -> new UserNotFound(USER_NOT_FOUND)
            );
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authUser.getUsername(), loginRequest.password())
            );
        } else {
            if (loginRequest.username() != null) {
                authUser = userRepository.findByUsername(loginRequest.username().toLowerCase()).orElseThrow(
                        () -> new UserNotFound(USER_NOT_FOUND)
                );
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.username().toLowerCase(), loginRequest.password())
                );
            } else {
                throw new IllegalArgumentException("Wrong login");
            }
        }

        String token = jwtUtil.generateToken(authUser);
        String refreshToken = jwtUtil.generateRefreshToken(authUser);

        ResponseCookie jwtCookie = generateCookies(token, refreshToken).getFirst();
        ResponseCookie refreshTokenCookie = generateCookies(token, refreshToken).get(1);
        var loginResponse = setCookiesAndLoginResponse(token, refreshToken, authUser);

        if (logger.isInfoEnabled()) {
            logger.info("Login response generated for username: {}", s);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString(), refreshTokenCookie.toString())
                .body(loginResponse);
    }

    public ResponseEntity<TokenResponse> refresh(TokenRefreshRequest tokenRefreshRequest, HttpServletRequest http) {
        try {
            String refreshToken = tokenRefreshRequest != null ? tokenRefreshRequest.refreshToken() : null;
            if (refreshToken == null) {
                refreshToken = getTokenFromCookie("refreshToken", http);
            }

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
            }

            String usernameToken = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByUsername(usernameToken)
                    .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

            String newAccessToken = jwtUtil.generateToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);
            var cookiesList = generateCookies(newAccessToken, newRefreshToken);
            ResponseCookie jwtCookie = cookiesList.getFirst();
            ResponseCookie refreshTokenCookie = cookiesList.get(1);
            userRepository.save(user);
            var loginResponse = TokenResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString(), refreshTokenCookie.toString())
                    .body(loginResponse);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, jwtUtil.getCleanJwtCookie().toString(), jwtUtil.getCleanRefreshTokenCookie().toString())
                    .body(null);
        }
    }

    public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            token = getTokenFromCookie("jwt", request);
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(MessageResponse.builder()
                            .message("Token is missing or invalid")
                            .timestamp(LocalDateTime.now())
                            .build());
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
                .body(MessageResponse.builder()
                        .message("Logout successful")
                        .timestamp(LocalDateTime.now())
                        .build());
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
