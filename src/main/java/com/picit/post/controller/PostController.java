package com.picit.post.controller;

import com.picit.post.controller.documentation.PostControllerDocumentation;
import com.picit.post.dto.PostDto;
import com.picit.post.dto.PostRequestDto;
import com.picit.post.services.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@AllArgsConstructor
public class PostController implements PostControllerDocumentation {

    private final PostService postService;

    @GetMapping("/user")
    public Page<PostDto> getPostUser(Authentication authentication, @RequestParam(required = false) String hobby, @RequestParam(defaultValue = "0") int page) {
        return postService.getPostsByUser(authentication.getName(), hobby, page);
    }

    @GetMapping
    public Page<PostDto> getPosts(Authentication authentication, @RequestParam(required = false) String hobby, @RequestParam(defaultValue = "0") int page) {
        return postService.getPosts(authentication.getName(), hobby, page);
    }

    @PostMapping
    public PostDto createPost(Authentication authentication, @Valid @RequestBody PostRequestDto postDto) {
        return postService.createPost(authentication.getName(), postDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(Authentication authentication, @PathVariable String id) {
        return postService.deletePost(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public PostDto updatePost(Authentication authentication, @PathVariable String id, @Valid @RequestBody PostRequestDto postDto) {
        return postService.updatePost(authentication.getName(), id, postDto);
    }

    @PostMapping("/search")
    public List<PostDto> searchPost(Authentication authentication, @RequestParam String search) {
        return postService.searchPost(authentication.getName(), search);
    }
}
