package com.picit.post.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {
    private UUID id;
    private String username;
    private String content;
    private LocalDateTime createdAt;

    public Comment(UUID id, String username, String content, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }
}