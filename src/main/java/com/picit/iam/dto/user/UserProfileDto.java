package com.picit.iam.dto.user;

import lombok.Builder;

@Builder
public record UserProfileDto(
        String bio,
        String[] hobbies,
        String[] follows
) {
}
