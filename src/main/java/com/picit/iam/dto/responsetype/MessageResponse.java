package com.picit.iam.dto.responsetype;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageResponse(
        String message,
        LocalDateTime timestamp
) {
}
