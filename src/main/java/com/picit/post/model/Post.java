package com.picit.post.model;

import com.picit.post.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Post {
    private UUID id;
    private String username;
    private String content;
    private String photoUrl;
    private List<UUID> likes;
    private List<Comment> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
