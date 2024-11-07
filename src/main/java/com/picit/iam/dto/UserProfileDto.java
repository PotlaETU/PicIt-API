package com.picit.iam.dto;

import lombok.Builder;

@Builder
public record UserProfileDto(
        String profilePicture,
        String bio,
        String[] hobbies,
        String[] follows
) {
}
