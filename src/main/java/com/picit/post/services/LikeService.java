package com.picit.post.services;

import com.picit.iam.entity.User;
import com.picit.iam.entity.points.PointDefinition;
import com.picit.iam.exceptions.PostNotFound;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserRepository;
import com.picit.iam.repository.points.PointsRepository;
import com.picit.post.dto.like.LikesDto;
import com.picit.post.entity.Likes;
import com.picit.post.entity.Post;
import com.picit.post.repository.LikesRepository;
import com.picit.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PointsRepository pointsRepository;
    private final MongoTemplate mongoTemplate;
    private static final String USER_NOT_FOUND = "User not found";

    public ResponseEntity<LikesDto> likePost(String username, String postId) {
        var userId = getUserId(username);
        if (likesRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            return ResponseEntity.ok(
                    LikesDto.builder()
                            .postId(postId)
                            .userId(userId)
                            .build());
        }

        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var like = Likes.builder()
                .postId(postId)
                .userId(userId)
                .build();

        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().addToSet("likes", like);
        mongoTemplate.updateFirst(query, update, Post.class);

        likesRepository.save(like);

        if (!"1".equals(post.getUserId())) {
            updatePoints(post.getUserId(), PointDefinition.LIKE_POST.getPoints());
        }

        return ResponseEntity.ok(
                LikesDto.builder()
                        .postId(postId)
                        .userId(userId)
                        .build());
    }

    public ResponseEntity<LikesDto> unlikePost(String username, String postId) {
        var userId = getUserId(username);

        var like = likesRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new PostNotFound("Like not found"));

        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));

        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().pull("likes",
                Query.query(Criteria.where("userId").is(userId)
                        .and("postId").is(postId)));
        mongoTemplate.updateFirst(query, update, Post.class);

        likesRepository.delete(like);

        if (!"1".equals(post.getUserId())) {
            updatePoints(post.getUserId(), -PointDefinition.DISLIKE_POST.getPoints());
        }

        return ResponseEntity.ok(
                LikesDto.builder()
                        .postId(postId)
                        .userId(userId)
                        .build());
    }

    private void updatePoints(String userId, int pointsToAdd) {
        var points = pointsRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        points.setPointsNb(points.getPointsNb() + pointsToAdd);
        pointsRepository.save(points);
    }

    private String getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
    }
}