package com.picit.post.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String username;
    private String content;
    private String photoUrl;
    private List<UUID> likes;
    private List<Comment> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
