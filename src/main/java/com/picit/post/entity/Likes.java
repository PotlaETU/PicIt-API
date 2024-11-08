package com.picit.post.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "likes")
public class Likes {
    @Id
    private String id;

    private String userId;

    private String postId;

    private String commentId;
}
