package com.picit.message.dto;

import com.picit.message.entity.RoomType;
import lombok.Builder;

import java.util.List;

@Builder
public record RoomDto(
        String id,
        RoomType type,
        List<String> users,
        MessageDto lastMessage
) {
}
