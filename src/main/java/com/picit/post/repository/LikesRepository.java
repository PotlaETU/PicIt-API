package com.picit.post.repository;

import com.picit.post.entity.Likes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LikesRepository extends MongoRepository<Likes, String> {
    Optional<Likes> findByUserIdAndPostId(String userId, String postId);
}
