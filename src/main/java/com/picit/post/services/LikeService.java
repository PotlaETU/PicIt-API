package com.picit.post.services;

import com.picit.iam.entity.User;
import com.picit.iam.entity.points.PointDefinition;
import com.picit.iam.exceptions.PostNotFound;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserRepository;
import com.picit.iam.repository.points.PointsRepository;
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
    private final PointsRepository pointsRepository;

    public ResponseEntity<Void> likePost(String username, String postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var points = pointsRepository.findByUserId(post.getUserId())
                .orElseThrow(() -> new UserNotFound("User not found"));
        points.setPoints(points.getPoints() + PointDefinition.LIKE_POST.getPoints());
        pointsRepository.save(points);

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
                .orElseThrow(() -> new PostNotFound("Like not found"));
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var points = pointsRepository.findByUserId(post.getUserId())
                .orElseThrow(() -> new UserNotFound("User not found"));
        points.setPoints(points.getPoints() - PointDefinition.DISLIKE_POST.getPoints());
        pointsRepository.save(points);

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
