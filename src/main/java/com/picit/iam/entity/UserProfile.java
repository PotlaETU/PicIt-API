package com.picit.iam.entity;

import com.picit.iam.entity.images.Image;
import com.picit.post.entity.Hobby;
import com.picit.iam.entity.images.Image;
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

    private Image profilePicture;

    private String bio;

    private List<Hobby> hobbies;

    private List<String> follows;

    private String userId;

    private String username;
}

