package com.picit.post.controller;

import com.picit.post.controller.documentation.CommentControllerDocumentation;
import com.picit.post.dto.CommentDto;
import com.picit.post.dto.comment.CommentRequestDto;
import com.picit.post.services.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController implements CommentControllerDocumentation {

    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(Authentication authentication,
                                    @Valid @RequestBody CommentRequestDto commentRequestDto,
                                    @RequestParam("postId") String postId) {
        return commentService.createComment(authentication.getName(), commentRequestDto, postId);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteComment(Authentication authentication,
                                              @RequestParam("commentId") String commentId,
                                              @RequestParam("postId") String postId) {
        return commentService.deleteComment(authentication.getName(), commentId, postId);
    }

    @PutMapping
    public ResponseEntity<CommentDto> updateComment(Authentication authentication, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.updateComment(authentication.getName(), commentRequestDto);
    }
}
