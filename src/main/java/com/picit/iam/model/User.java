package com.picit.iam.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Document(collection = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User implements UserDetails {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

}
