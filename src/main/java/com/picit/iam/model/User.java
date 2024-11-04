package com.picit.iam.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    private String profilePicture;

    private String bio;

    private String[] hobbies;

    private String[] follows;

    private Settings settings;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
