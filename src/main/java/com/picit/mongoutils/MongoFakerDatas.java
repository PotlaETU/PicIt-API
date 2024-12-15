package com.picit.mongoutils;

import com.picit.iam.entity.Settings;
import com.picit.iam.entity.User;
import com.picit.iam.entity.UserProfile;
import com.picit.iam.entity.UserRole;
import com.picit.iam.repository.UserRepository;
import com.picit.post.entity.Hobby;
import com.picit.post.entity.Post;
import com.picit.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MongoFakerDatas {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final Faker faker = this.faker();

    @Bean
    public Faker faker() {
        return new Faker(Locale.FRANCE);
    }

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
        return faker.lorem().paragraph(3);
    }

    public User buildFakeUser() {
        return User.builder()
                .email(getFakerEmail())
                .username(getFakerUsername())
                .password(getFakerPassword())
                .role(UserRole.ROLE_FAKE_USER)
                .userProfile(UserProfile.builder()
                        .followers(List.of(faker.internet().uuid(), faker.internet().uuid(), faker.internet().uuid(), faker.internet().uuid()))
                        .username(getFakerUsername())
                        .bio(faker.lorem().characters(10, 20))
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
                .isPublic(true)
                .hobby(Hobby.DETENTE)
                .userId("1")
                .build();
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            String activeProfile = System.getProperty("spring.profiles.active");
            if ("production".equals(activeProfile) || "test".equals(activeProfile)) {
                log.info("Skipping fake data generation in {} profile", activeProfile);
                return;
            }
            log.info("Generating {} fake users", 10);
            for (int i = 0; i < 10; i++) {
                User user = this.buildFakeUser();
                userRepository.save(user);
            }
            log.info("Generating {} fake posts", 10);
            for (int i = 0; i < 10; i++) {
                Post post = this.buildFakePost();
                postRepository.save(post);
            }
        };
    }
}
