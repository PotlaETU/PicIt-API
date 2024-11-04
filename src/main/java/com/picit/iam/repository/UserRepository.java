package com.picit.iam.repository;

import com.picit.iam.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(@NotBlank(message = "Username is required") String username);
}
