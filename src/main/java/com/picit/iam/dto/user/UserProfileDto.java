package com.picit.iam.dto.user;

import com.picit.post.entity.Hobby;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileDto(
        String username,
        String bio,
        List<Hobby> hobbies,
        List<String> follows,
        List<String> followers,
        List<String> blockedUsers,
        String userId,
        Long postCount,
        int points
) {
}
