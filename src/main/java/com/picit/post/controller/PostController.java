package com.picit.post.controller;

import com.picit.post.dto.PostDto;
import com.picit.post.entity.Post;
import com.picit.post.mapper.PostMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
@AllArgsConstructor
public class PostController {

    private final PostMapper postMapper;

    @GetMapping
    public PostDto getPost() {
        return postMapper.postToPostDto(new Post());
    }
}
