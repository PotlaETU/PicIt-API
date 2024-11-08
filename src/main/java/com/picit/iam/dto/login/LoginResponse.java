package com.picit.iam.dto.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.picit.iam.dto.token.TokenResponse;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        TokenResponse token,
        String expiration,
        String username,
        String email
) {
}
