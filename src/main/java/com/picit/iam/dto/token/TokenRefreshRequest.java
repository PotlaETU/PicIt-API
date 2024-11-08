package com.picit.iam.dto.token;

import lombok.Builder;

@Builder
public record TokenRefreshRequest(
    String refreshToken
) {
}
