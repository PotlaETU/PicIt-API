package com.picit.iam.controller;

import com.picit.iam.dto.*;
import com.picit.iam.services.IamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return iamService.refresh(tokenRefreshRequest);
    }

    @GetMapping("/me")
    public ResponseEntity<String> getMe() {
        return ResponseEntity.ok("c'est bon tu es connecté");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return iamService.logout(request);
    }
}
