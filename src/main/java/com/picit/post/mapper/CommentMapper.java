package com.picit.post.mapper;

import com.picit.post.dto.CommentDto;
import com.picit.post.dto.comment.CommentRequestDto;
import com.picit.post.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "username", ignore = true)
    Comment commentRequestDtoToComment(CommentRequestDto commentRequestDto, String userId, String postId);

    CommentDto commentToCommentDto(Comment commentSaved);
}
