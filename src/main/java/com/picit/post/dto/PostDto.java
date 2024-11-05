package com.picit.post.dto;

import com.picit.post.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostDto (
    UUID id,
    String username,
    String content,
    String photoUrl,
    List<UUID> likes,
    List<Comment> comments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
