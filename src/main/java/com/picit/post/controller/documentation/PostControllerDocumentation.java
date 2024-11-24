package com.picit.post.controller.documentation;

import com.picit.post.dto.PostDto;
import com.picit.post.dto.request.PostRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "Post", description = "Post management")
public interface PostControllerDocumentation {
    @Operation(summary = "Get post by user", description = "Fetches a post by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    Page<PostDto> getPostUser(Authentication authentication, String hobby, int page);

    @Operation(summary = "Get post", description = "Fetches a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    Page<PostDto> getPosts(Authentication authentication, String hobby, int page);


    @Operation(summary = "Create post", description = "Creates a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    PostDto createPost(Authentication authentication, PostRequestDto postDto);

    @Operation(summary = "Delete post", description = "Deletes a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    ResponseEntity<Void> deletePost(Authentication authentication, String id);

    @Operation(summary = "Update post", description = "Updates a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    PostDto updatePost(Authentication authentication, String id, PostRequestDto postDto);

    @Operation(summary = "Search post", description = "Searches a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post searched successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    List<PostDto> searchPost(Authentication authentication, String search);
}