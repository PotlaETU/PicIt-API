package com.picit.post.repository.postpic;

import com.picit.post.entity.postimage.PostImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostImageRepository extends MongoRepository<PostImage, String> {
    PostImage findByPostId(String postId);
}
