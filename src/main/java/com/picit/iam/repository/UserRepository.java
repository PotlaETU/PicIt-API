package com.picit.iam.repository;

import com.picit.iam.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(@NotBlank(message = "Username is required") String username);

    Optional<User> findByUsername(@NotBlank(message = "Username is required") String username);

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);

    List<User> findAllByUpdatedAtAfter(LocalDateTime lastSyncDate);

    Optional<User> findByEmail(String email);
}
