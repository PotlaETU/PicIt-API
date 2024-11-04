package com.picit.iam.mapper;

import com.picit.iam.dto.SignUpRequest;
import com.picit.iam.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(SignUpRequest signUpRequest);
}
