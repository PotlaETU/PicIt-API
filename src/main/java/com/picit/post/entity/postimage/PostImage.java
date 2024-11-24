package com.picit.post.entity.postimage;

import com.picit.iam.entity.images.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@Document(collection = "postImage")
public class PostImage extends Image {
    private String userId;
    private String postId;
    private String description;

    public PostImage() {
    }
}
