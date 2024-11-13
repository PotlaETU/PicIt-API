package com.picit.post.controller.documentation;

import com.picit.post.dto.CommentDto;
import com.picit.post.dto.comment.CommentRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Comment", description = "Comment management")
public interface CommentControllerDocumentation {

    CommentDto createComment(Authentication authentication, CommentRequestDto commentRequestDto);

    ResponseEntity<Void> deleteComment(Authentication authentication, String commentId);

    ResponseEntity<Void> updateComment(Authentication authentication, CommentRequestDto commentRequestDto);
}
