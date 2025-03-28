package com.picit.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
        String id,
        String userId,
        String username,
        String content,
        LocalDateTime createdAt
) {

}
