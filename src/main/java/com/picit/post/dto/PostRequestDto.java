package com.picit.post.dto;

import com.picit.post.entity.Hobby;
import lombok.Builder;

@Builder
public record PostRequestDto(
        String content,
        String photoUrl,
        Hobby hobby,
        Boolean isPublic
) {
}
