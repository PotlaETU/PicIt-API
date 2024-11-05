package com.picit.message.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String sender_id;
    private String room_id;
    private String content;
    private LocalDateTime createdAt;
}
