package com.picit.iam.controller;

import com.picit.iam.dto.*;
import com.picit.iam.services.IamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam")
@AllArgsConstructor
public class IamController {

    private final IamService iamService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return iamService.signUp(signUpRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return iamService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRefreshRequest tokenRefreshRequest, Authentication authentication) {
        return iamService.refresh(tokenRefreshRequest, authentication.getName());
    }

    @GetMapping("/me")
    public UserDto getMe(Authentication authentication) {
        return iamService.getUser(authentication.getName());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return iamService.logout(request);
    }
}
