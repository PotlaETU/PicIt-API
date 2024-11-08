package com.picit.post.controller;

import com.picit.post.dto.PostDto;
import com.picit.post.dto.PostRequestDto;
import com.picit.post.services.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/user")
    public List<PostDto> getPostUser(Authentication authentication, @RequestParam(required = false) String hobby) {
        return postService.getPostsByUser(authentication.getName(), hobby);
    }

    @GetMapping
    public List<PostDto> getPosts(Authentication authentication, @RequestParam(required = false) String hobby) {
        return postService.getPosts(authentication.getName(), hobby);
    }

    @PostMapping("/add")
    public List<PostDto> addPost(Authentication authentication, @Valid @RequestBody PostDto postDto) {
        return postService.addPost(authentication.getName(), postDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(Authentication authentication, @PathVariable String id) {
        return postService.deletePost(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public PostDto updatePost(Authentication authentication, @PathVariable String id, @Valid @RequestBody PostRequestDto postDto) {
        return postService.updatePost(authentication.getName(), id, postDto);
    }
}
