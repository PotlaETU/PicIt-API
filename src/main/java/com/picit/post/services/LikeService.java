package com.picit.post.services;

import com.picit.iam.entity.User;
import com.picit.iam.exceptions.PostNotFound;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserRepository;
import com.picit.post.entity.Likes;
import com.picit.post.repository.LikesRepository;
import com.picit.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Void> likePost(String username, String postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var userId = getUserId(username);
        var like = Likes.builder()
                .postId(postId)
                .userId(userId)
                .build();
        likesRepository.save(like);
        post.getLikes().add(like);
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> unlikePost(String name, String postId) {
        var userId = getUserId(name);
        var like = likesRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        likesRepository.delete(like);
        post.getLikes().remove(like);
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    private String getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));
    }
}
