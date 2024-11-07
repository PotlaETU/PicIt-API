package com.picit.iam.entity;

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

    private String profilePicture;

    private String bio;

    private String[] hobbies;

    private String[] follows;

    private String userId;
}

