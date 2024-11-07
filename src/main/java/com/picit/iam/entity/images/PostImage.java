package com.picit.iam.entity.images;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@Document(collection = "postImage")
public class PostImage {
    private String userId;
    private String postId;
    private String description;
}
