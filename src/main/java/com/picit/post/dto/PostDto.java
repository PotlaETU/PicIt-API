package com.picit.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.picit.post.entity.Comment;
import com.picit.post.entity.Hobby;
import com.picit.post.entity.Likes;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostDto(
        String id,
        String userId,
        String content,
        String photoUrl,
        Hobby hobby,
        Boolean isPublic,
        List<Likes> likes,
        List<Comment> comments,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
