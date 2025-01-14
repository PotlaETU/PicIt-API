package com.picit.post.controller;

import com.picit.iam.dto.responsetype.MessageResponse;
import com.picit.post.controller.documentation.PostControllerDocumentation;
import com.picit.post.dto.PostDto;
import com.picit.post.dto.request.PostImageRequestDto;
import com.picit.post.dto.request.PostRequestDto;
import com.picit.post.services.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@AllArgsConstructor
public class PostController implements PostControllerDocumentation {

    private final PostService postService;

    @GetMapping("/user")
    public List<PostDto> getPostUser(Authentication authentication,
                                     @RequestParam(required = false, name = "hobby") String hobby,
                                     @RequestParam(defaultValue = "0", name = "page") int page) {
        return postService.getPostsByUser(authentication.getName(), hobby, page);
    }

    @GetMapping
    public List<PostDto> getPosts(Authentication authentication,
                                  @RequestParam(required = false, name = "hobby") String hobby,
                                  @RequestParam(defaultValue = "0", name = "page") int page) {
        return postService.getPosts(authentication.getName(), hobby, page);
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getPostsByUsername(Authentication authentication,
                                  @PathVariable String userId,
                                  @RequestParam(required = false, name = "hobby") String hobby,
                                  @RequestParam(defaultValue = "0", name = "page") int page) {
        return postService.getPostsByUsername(authentication.getName(), userId, hobby, page);
    }

    @GetMapping("/{id}")
    public PostDto getPost(Authentication authentication, @PathVariable String id) {
        return postService.getPost(authentication.getName(), id);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getPostImage(Authentication authentication, @PathVariable String id) {
        return postService.getPostImage(authentication.getName(), id);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public PostDto createPost(
            Authentication authentication,
            @Valid @RequestPart(value = "postData") PostRequestDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return postService.createPost(authentication.getName(), postDto, null, file);
    }

    @PostMapping(path = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PostDto createPostJson(
            Authentication authentication,
            @Valid @RequestBody PostRequestDto postDto) {
        return postService.createPost(authentication.getName(), null, postDto, null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletePost(Authentication authentication, @PathVariable String id) {
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

    @PostMapping("/image")
    public ResponseEntity<String> addPostImage(Authentication authentication,
                                               @RequestParam(value = "file", required = false) MultipartFile file,
                                               @RequestParam("postId") String postId,
                                               @RequestParam(value = "aiGenerated", required = false, defaultValue = "false") Boolean aiGenerated,
                                               @RequestBody(required = false) PostImageRequestDto postImageRequestDto) {
        return postService.setPostImage(file, authentication.getName(), postId, aiGenerated, postImageRequestDto);
    }
}