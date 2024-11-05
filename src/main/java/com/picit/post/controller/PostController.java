package com.picit.post.controller;

import com.picit.post.dto.PostDto;
import com.picit.post.mapper.PostMapper;
import com.picit.post.model.Post;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/post")
public class PostController {
    private final PostMapper postMapper = PostMapper.INSTANCE;

    @GetMapping
    public PostDto getPost() {
        return postMapper.postToPostDto(new Post());
    }
}
