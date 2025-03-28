package com.picit.iam.entity;

import com.picit.iam.entity.images.ProfilePicImage;
import com.picit.post.entity.Hobby;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Document(collection = "userProfile")
public class UserProfile {

    @Id
    private String id;

    private ProfilePicImage profilePicture;

    private String bio;

    private List<Hobby> hobbies;

    private List<String> follows;

    private List<String> followers;

    private List<String> blockedUsers;

    private String userId;

    private String username;
}

