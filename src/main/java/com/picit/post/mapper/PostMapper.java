package com.picit.post.mapper;

import com.picit.post.dto.PostDto;
import com.picit.post.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    Post postDtoToPost(PostDto postDto);;
    PostDto postToPostDto(Post post);
}
