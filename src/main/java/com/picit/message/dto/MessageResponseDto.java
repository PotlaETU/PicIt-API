package com.picit.message.dto;

import lombok.Builder;

@Builder
public record MessageResponseDto(
        String username,
        String content
) {
}
