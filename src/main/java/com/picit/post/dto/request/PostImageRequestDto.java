package com.picit.post.dto.request;

import lombok.Builder;

@Builder
public record PostImageRequestDto(
        String description,
        String prompt
) {
}
