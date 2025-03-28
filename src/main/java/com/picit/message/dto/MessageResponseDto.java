package com.picit.message.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageResponseDto(
        String username,
        String content,
        String roomId,
        Boolean isSeen,
        LocalDateTime createdAt
) {
}
