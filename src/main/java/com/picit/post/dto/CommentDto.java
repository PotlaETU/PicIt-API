package com.picit.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentDto(
        String id,
        String userId,
        String content,
        LocalDateTime createdAt
) {

}
