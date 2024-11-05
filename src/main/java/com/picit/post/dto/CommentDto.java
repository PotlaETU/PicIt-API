package com.picit.post.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentDto (
    UUID id,
    String username,
    String content,
    LocalDateTime createdAt
) {

}
