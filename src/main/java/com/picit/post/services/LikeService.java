package com.picit.post.services;

import com.picit.iam.entity.User;
import com.picit.iam.entity.points.PointDefinition;
import com.picit.iam.exceptions.PostNotFound;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserRepository;
import com.picit.iam.repository.points.PointsRepository;
import com.picit.post.dto.like.LikesDto;
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
    private static final String USER_NOT_FOUND = "User not found";

    public ResponseEntity<LikesDto> likePost(String username, String postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        if (!"1".equals(post.getUserId())) {
            var points = pointsRepository.findByUserId(post.getUserId())
                    .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
            points.setPointsNb(points.getPointsNb() + PointDefinition.LIKE_POST.getPoints());
            pointsRepository.save(points);
        }
        var userId = getUserId(username);

        if (likesRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            return ResponseEntity.ok(
                    LikesDto.builder()
                            .postId(postId)
                            .userId(userId)
                            .build());
        }
        var like = Likes.builder()
                .postId(postId)
                .userId(userId)
                .build();
        likesRepository.save(like);
        post.getLikes().add(like);
        postRepository.save(post);
        return ResponseEntity.ok(
                LikesDto.builder()
                        .postId(postId)
                        .userId(userId)
                        .build());
    }

    public ResponseEntity<Void> unlikePost(String name, String postId) {
        var userId = getUserId(name);
        var like = likesRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new PostNotFound("Like not found"));
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var points = pointsRepository.findByUserId(post.getUserId())
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        points.setPointsNb(points.getPointsNb() - PointDefinition.DISLIKE_POST.getPoints());
        pointsRepository.save(points);

        likesRepository.delete(like);
        post.getLikes().remove(like);
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    private String getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
    }
}
