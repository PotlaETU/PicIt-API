package com.picit.message.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageDto (
        String id,
        String senderUsername,
        String roomId,
        String content,
        LocalDateTime createdAt
) {

}
