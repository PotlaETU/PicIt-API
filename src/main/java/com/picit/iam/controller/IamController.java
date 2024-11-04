package com.picit.iam.controller;

import com.picit.iam.dto.SignUpRequest;
import com.picit.iam.services.IamService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam")
@AllArgsConstructor
public class IamController {

    private final IamService iamService;

    @PostMapping("/register")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        this.iamService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        return ResponseEntity.ok().build();
    }
}
