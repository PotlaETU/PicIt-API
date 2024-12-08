package com.picit.iam.dto.responseType;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageResponse(
        String message,
        LocalDateTime timestamp
) {
}
