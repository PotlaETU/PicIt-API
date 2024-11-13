package com.picit.message.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageDto (
        String id,
        String senderId,
        String roomId,
        String content,
        LocalDateTime createdAt
) {

}
