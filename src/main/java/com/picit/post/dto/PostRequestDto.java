package com.picit.post.dto;

import com.picit.post.entity.Hobby;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PostRequestDto(
        @NotBlank(message = "Content is required")
        String content,
        String photoUrl,
        @NotNull(message = "Hobby is required")
        Hobby hobby,
        @NotNull(message = "Visibility is required")
        Boolean isPublic
) {
}
