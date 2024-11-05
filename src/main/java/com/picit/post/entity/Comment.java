package com.picit.post.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Comment {
    private UUID id;
    private String username;
    private String content;
    private LocalDateTime createdAt;
}