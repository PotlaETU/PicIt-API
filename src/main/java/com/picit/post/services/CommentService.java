package com.picit.post.services;

import com.picit.iam.entity.User;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserRepository;
import com.picit.post.dto.CommentDto;
import com.picit.post.dto.comment.CommentRequestDto;
import com.picit.post.entity.Comment;
import com.picit.post.mapper.CommentMapper;
import com.picit.post.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private static final String COMMENT_NOT_FOUND = "Comment not found";
    private static final String USER_NOT_FOUND = "User not found";

    public CommentDto createComment(String username, CommentRequestDto commentRequestDto) {
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        Comment comment = commentMapper.commentRequestDtoToComment(commentRequestDto, userId);
        var commentSaved = commentRepository.save(comment);
        return commentMapper.commentToCommentDto(commentSaved);
    }

    public ResponseEntity<Void> deleteComment(String username, String commentId) {
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new UserNotFound(COMMENT_NOT_FOUND));

        if (!userId.equals(comment.getUserId())) {
            return ResponseEntity.status(403).build();
        } else {
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<CommentDto> updateComment(String name, CommentRequestDto commentRequestDto) {
        var userId = userRepository.findByUsername(name)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        var comment = commentRepository.findById(commentRequestDto.commentId())
                .orElseThrow(() -> new UserNotFound(COMMENT_NOT_FOUND));
        if (comment.getContent().isBlank()) {
            return ResponseEntity.status(400).build();
        }
        if (comment.getUserId().equals(userId)) {
            comment.setContent(commentRequestDto.content());
            var savedComment = commentRepository.save(comment);
            var commentDto = commentMapper.commentToCommentDto(savedComment);
            return ResponseEntity.ok().body(commentDto);
        } else {
            return ResponseEntity.status(403).build();
        }
    }
}
