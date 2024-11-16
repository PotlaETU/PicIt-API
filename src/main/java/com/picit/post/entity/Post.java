package com.picit.post.entity;

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
    private String photoUrl;
    private Hobby hobby;
    private List<Likes> likes;
    private Boolean isPublic;
    private String userId;
    private List<Comment> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
