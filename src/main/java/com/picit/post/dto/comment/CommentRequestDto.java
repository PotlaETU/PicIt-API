package com.picit.post.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
        @NotBlank(message = "Content is required")
        String content
) {
}
