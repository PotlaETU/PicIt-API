package com.picit.mongoutils;

import com.picit.iam.entity.User;
import com.picit.iam.repository.UserRepository;
import com.picit.post.entity.Post;
import com.picit.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MongoSyncService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final RestTemplate restTemplate;

    private LocalDateTime lastSyncDate = LocalDateTime.now();

    @Value("${neo4j-sync.uri}")
    private String neo4JUrl;

    public MongoSyncService(UserRepository userRepository, PostRepository postRepository, RestTemplateBuilder builder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.restTemplate = builder.build();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void syncMongoWithNeo4j() {
        List<User> usersToSync = userRepository.findAllByUpdatedAtAfter(lastSyncDate);
        List<Post> postsToSync = postRepository.findAllByUpdatedAtAfter(lastSyncDate);

        lastSyncDate = LocalDateTime.now();

        restTemplate.postForObject(neo4JUrl + "/users", usersToSync, Void.class);
        restTemplate.postForObject(neo4JUrl + "/posts", postsToSync, Void.class);
    }

}
