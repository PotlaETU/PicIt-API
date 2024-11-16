package com.picit.post.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;

    private String userId;

    private String content;

    private LocalDateTime createdAt;
}