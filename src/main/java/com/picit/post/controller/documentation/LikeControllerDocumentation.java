package com.picit.post.controller.documentation;

import com.picit.post.dto.like.LikesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Like", description = "Like management")
public interface LikeControllerDocumentation {

    @Operation(summary = "Like a post", description = "Make the user like a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like set"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    ResponseEntity<LikesDto> likePost(Authentication authentication, String postId);

    @Operation(summary = "Unlike a post", description = "Make the user unlike a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like removed"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    ResponseEntity<Void> unlikePost(Authentication authentication, String postId);
}
