package com.picit.iam.repository;

import com.picit.iam.entity.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    UserProfile findByUserId(String userId);

    Optional<UserProfile> findByUsername(String username);

    Optional<List<UserProfile>> findByUsernameRegex(String query);
}
