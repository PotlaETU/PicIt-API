package com.picit.mongosync;

import com.picit.iam.entity.User;
import com.picit.iam.repository.UserRepository;
import com.picit.post.entity.Post;
import com.picit.post.repository.PostRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class MongoSyncService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final RestTemplate restTemplate;

    private LocalDateTime lastSyncDate = LocalDateTime.now();

    @Value("${neo4j-sync.uri}")
    private String NEO4J_URL;

    public MongoSyncService(UserRepository userRepository, PostRepository postRepository, RestTemplateBuilder builder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.restTemplate = builder.build();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void syncMongoWithNeo4j() {
        List<User> usersToSync = userRepository.findAllByUpdatedAtAfter(lastSyncDate);
        List<Post> postsToSync = postRepository.findAllByUpdatedAtAfter(lastSyncDate);

        lastSyncDate = LocalDateTime.now();

        restTemplate.postForObject(NEO4J_URL + "/sync-neo4j/users", usersToSync, Void.class);
        restTemplate.postForObject(NEO4J_URL + "/sync-neo4j/posts", postsToSync, Void.class);
    }

}
