package com.picit.iam.controller;

import com.picit.iam.controller.documentation.IamControllerDocumentation;
import com.picit.iam.dto.login.LoginRequest;
import com.picit.iam.dto.login.LoginResponse;
import com.picit.iam.dto.login.SignUpRequest;
import com.picit.iam.dto.responseType.MessageResponse;
import com.picit.iam.dto.token.TokenRefreshRequest;
import com.picit.iam.dto.token.TokenResponse;
import com.picit.iam.dto.user.UserDto;
import com.picit.iam.services.IamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam")
@AllArgsConstructor
public class IamController implements IamControllerDocumentation {

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
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Nullable TokenRefreshRequest tokenRefreshRequest,
                                                 Authentication authentication,
                                                 HttpServletRequest request) {
        return iamService.refresh(tokenRefreshRequest, authentication.getName(), request);
    }

    @GetMapping("/me")
    public UserDto getMe(Authentication authentication) {
        return iamService.getUser(authentication.getName());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return iamService.logout(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestParam(name = "oldPassword") String oldPassword,
                                                         @RequestParam(name = "newPassword") String newPassword,
                                                         Authentication authentication) {
        return iamService.resetPassword(oldPassword, newPassword, authentication.getName());
    }
}
