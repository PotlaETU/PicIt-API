package com.picit.post.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
        @NotBlank(message = "commentId is required")
        String commentId,
        @NotBlank(message = "text is required")
        String content
) {
}
