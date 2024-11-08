package com.picit.iam.dto.user;

import com.picit.iam.entity.Settings;
import com.picit.iam.entity.images.Image;
import lombok.Builder;

@Builder
public record UserDto(
        String id,
        String username,
        String email,
        Image profilePicture,
        String bio,
        String[] hobbies,
        String[] follows,
        Settings settings,
        String createdAt,
        String updatedAt
) {
}
