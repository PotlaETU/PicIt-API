package com.picit.mongoutils;

import com.picit.iam.entity.Settings;
import com.picit.iam.entity.User;
import com.picit.post.entity.Hobby;
import com.picit.post.entity.Post;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class MongoFakerDatas {
    Faker faker = new Faker(Locale.FRANCE);

    private String getFakerUsername() {
        return faker.internet().username();
    }

    private String getFakerEmail() {
        return faker.internet().emailAddress();
    }

    private String getFakerPassword() {
        return faker.internet().password();
    }

    private String getFakerContent() {
        return faker.lorem().characters(10, 20);
    }

    private String getFakerTitle() {
        return faker.lorem().characters(5, 10);
    }

    public User buildFakeUser() {
        return User.builder()
                .email(getFakerEmail())
                .username(getFakerUsername())
                .password(getFakerPassword())
                .settings(Settings.builder()
                        .notifications(true)
                        .privacy("public")
                        .build())
                .build();
    }

    public Post buildFakePost() {
        return Post.builder()
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .content(getFakerContent())
                .hobby(Hobby.DETENTE)
                .userId("1")
                .build();
    }
}
