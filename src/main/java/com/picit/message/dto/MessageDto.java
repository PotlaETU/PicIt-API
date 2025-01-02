package com.picit.message.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageDto (
        String senderUsername,
        String content,
        LocalDateTime createdAt
) {

}
