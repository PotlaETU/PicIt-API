package com.picit.post.dto.like;

import lombok.Builder;

@Builder
public record LikesDto(
        String postId,
        String userId,
        String commentId
) {
}
