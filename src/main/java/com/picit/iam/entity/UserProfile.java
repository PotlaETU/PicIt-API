package com.picit.iam.entity;

import com.picit.iam.entity.images.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Document(collection = "userProfile")
public class UserProfile {

    @Id
    private String id;

    private Image profilePicture;

    private String bio;

    private String[] hobbies;

    private String[] follows;

    private String userId;
}

