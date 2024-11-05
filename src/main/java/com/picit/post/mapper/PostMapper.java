package com.picit.post.mapper;

import com.picit.post.dto.PostDto;
import com.picit.post.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post postDtoToPost(PostDto postDto);

    PostDto postToPostDto(Post post);
}
