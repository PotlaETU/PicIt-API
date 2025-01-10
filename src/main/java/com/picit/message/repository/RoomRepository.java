package com.picit.message.repository;

import com.picit.iam.entity.User;
import com.picit.message.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findByUsers(Set<User> users);

    List<Room> findByUsersContaining(User user);
}
