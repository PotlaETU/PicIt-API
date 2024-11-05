package com.picit.iam.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String token,
        String expiration,
        String refreshToken,
        String username,
        String email
) {
}
