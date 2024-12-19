package com.picit.iam.mapper;

import com.picit.iam.dto.login.SignUpRequest;
import com.picit.iam.dto.user.UserDto;
import com.picit.iam.dto.user.UserProfileDto;
import com.picit.iam.entity.User;
import com.picit.iam.entity.UserProfile;
import com.picit.iam.entity.points.Points;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(com.picit.iam.entity.UserRole.ROLE_USER)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "settings", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    User toUser(SignUpRequest signUpRequest);

    @Mapping(target = "points", ignore = true)
    @Mapping(target = "postCount", ignore = true)
    UserProfileDto toUserProfileDto(UserProfile userProfile);

    @Mapping(target = "points", source = "points.pointsNb")
    UserProfileDto toUserProfileDto(UserProfile userProfile, Points points, Long postCount);


    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "profilePicture", ignore = true)
    UserDto toUserDto(User user, UserProfile userProfile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "hobbies", ignore = true)
    @Mapping(target = "follows", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "followers", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "blockedUsers", ignore = true)
    UserProfile toUserProfile(User user);
}
