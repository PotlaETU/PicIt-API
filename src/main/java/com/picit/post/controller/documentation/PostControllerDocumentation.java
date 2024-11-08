package com.picit.post.controller.documentation;

import com.picit.post.dto.PostDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Post", description = "Post management")
public interface PostControllerDocumentation {

    @Operation(summary = "Get post", description = "Fetches a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    List<PostDto> getPosts(Authentication authentication, @RequestParam(required = false) String hobby);
}