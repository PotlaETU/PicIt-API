package com.picit.iam.dto;

import lombok.Builder;

@Builder
public record TokenResponse(
        String token,
        String refreshToken
) {
}
