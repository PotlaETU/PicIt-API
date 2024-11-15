package com.picit.iam.dto.user;

import com.picit.post.entity.Hobby;
import lombok.Builder;

import java.util.List;

@Builder
public record SuggestedUserDto(
        UserProfileDto userProfile,
        List<Hobby> commonHobbies
) {
}
