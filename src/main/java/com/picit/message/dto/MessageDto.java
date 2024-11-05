package com.picit.message.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageDto (
        String id,
        String sender_id,
        String room_id,
        String content,
        LocalDateTime createdAt
) {

}
