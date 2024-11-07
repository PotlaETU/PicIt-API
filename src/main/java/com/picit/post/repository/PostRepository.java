package com.picit.post.repository;

import com.picit.post.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findAllByUpdatedAtAfter(LocalDateTime lastSyncDate);
}
