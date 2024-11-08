package com.picit.iam.dto.token;

import lombok.Builder;

@Builder
public record TokenResponse(
        String token,
        String refreshToken
) {
}
