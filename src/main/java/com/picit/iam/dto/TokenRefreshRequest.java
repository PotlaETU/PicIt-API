package com.picit.iam.dto;

import lombok.Builder;

@Builder
public record TokenRefreshRequest(
    String refreshToken
) {
}
