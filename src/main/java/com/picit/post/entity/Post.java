package com.picit.post.entity;

import com.picit.post.entity.postimage.PostImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String content;
    private PostImage postImage;
    private Hobby hobby;
    private List<Likes> likes;
    private Boolean isPublic;
    private String userId;
    private List<Comment> comments;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}