package com.picit.post.repository;

import com.picit.post.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findAllByUpdatedAtAfter(LocalDateTime lastSyncDate);

    Optional<List<Post>> findPostsByContentRegex(String content);

    Boolean existsPostByUserIdAndCreatedAtAfter(String userId, LocalDateTime startOfDay);
}
