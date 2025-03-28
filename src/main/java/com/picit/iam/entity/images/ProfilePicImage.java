package com.picit.iam.entity.images;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@Document(collection = "profilePicImage")
public class ProfilePicImage extends Image {
    private String userId;

    public ProfilePicImage() {
        // Needed to retrieve the object from the database
    }
}
