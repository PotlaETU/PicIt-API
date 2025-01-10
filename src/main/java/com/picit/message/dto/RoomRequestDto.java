package com.picit.message.dto;

import com.picit.message.entity.RoomType;
import lombok.Builder;

@Builder
public record RoomRequestDto(
        String username,
        RoomType type
) {
}
