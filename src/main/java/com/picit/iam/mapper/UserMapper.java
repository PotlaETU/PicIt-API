package com.picit.iam.mapper;

import com.picit.iam.dto.login.SignUpRequest;
import com.picit.iam.dto.user.UserDto;
import com.picit.iam.dto.user.UserProfileDto;
import com.picit.iam.entity.User;
import com.picit.iam.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(\"USER\")")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "settings", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    User toUser(SignUpRequest signUpRequest);

    UserProfileDto toUserProfileDto(UserProfile userProfile);

    @Mapping(target = "id", source = "user.id")
    UserDto toUserDto(User user, UserProfile userProfile);
}
