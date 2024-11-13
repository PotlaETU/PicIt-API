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

    public CommentDto createComment(String username, CommentRequestDto commentRequestDto) {
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));

        Comment comment = commentMapper.commentRequestDtoToComment(commentRequestDto, userId);
        var commentSaved = commentRepository.save(comment);
        return commentMapper.commentToCommentDto(commentSaved);
    }

    public ResponseEntity<Void> deleteComment(String username, String commentId) {
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new UserNotFound("Comment not found"));

        if (!userId.equals(comment.getUserId())){
            return ResponseEntity.status(403).build();
        } else {
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<Void> updateComment(String name, CommentRequestDto commentRequestDto) {
        return null;
    }
}
