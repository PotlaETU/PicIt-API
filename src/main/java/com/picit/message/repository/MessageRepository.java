package com.picit.message.repository;

import com.picit.message.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByRoomId(String roomId);
}
