package com.picit.iam.controller;

import com.picit.iam.dto.LoginRequest;
import com.picit.iam.dto.SignUpRequest;
import com.picit.iam.services.IamService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam")
@AllArgsConstructor
public class IamController {

    private final IamService iamService;

    @PostMapping("/register")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        this.iamService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = iamService.login(loginRequest);
        return ResponseEntity.ok().body(token);
    }

    @GetMapping("/me")
    public ResponseEntity<Void> getMe() {
        return ResponseEntity.ok().build();
    }
}
