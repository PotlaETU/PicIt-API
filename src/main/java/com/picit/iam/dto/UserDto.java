package com.picit.iam.dto;

import com.picit.iam.model.Settings;
import lombok.Builder;

@Builder
public record UserDto(
        String id,
        String username,
        String email,
        String profilePicture,
        String bio,
        String[] hobbies,
        String[] follows,
        Settings settings,
        String createdAt,
        String updatedAt
) {
}
