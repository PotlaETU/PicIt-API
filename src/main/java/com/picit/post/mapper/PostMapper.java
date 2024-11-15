package com.picit.post.mapper;

import com.picit.post.dto.PostDto;
import com.picit.post.dto.PostRequestDto;
import com.picit.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostDto postToPostDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Post postRequestDtoToPost(PostRequestDto postRequestDto, String id);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Post updatePostFromPostRequestDto(PostRequestDto postRequestDto, @MappingTarget Post post);
}
