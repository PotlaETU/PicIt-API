package com.picit.iam.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        String email,
        String username,

        @NotBlank(message = "Password is required")
        String password) {

    public LoginRequest {
        if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
            throw new IllegalArgumentException("Either username or email must be provided");
        }
    }

}
