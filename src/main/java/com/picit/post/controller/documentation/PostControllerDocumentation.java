package com.picit.post.controller.documentation;

import com.picit.post.dto.PostDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Post", description = "Post management")
public interface PostControllerDocumentation {

    @Operation(summary = "Get post", description = "Fetches a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    PostDto getPost();
}