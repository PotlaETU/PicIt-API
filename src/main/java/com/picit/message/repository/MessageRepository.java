package com.picit.message.repository;

import com.picit.message.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByRoomId(String roomId);

    List<Message> findByRoomIdIn(Set<String> roomIds);
}
