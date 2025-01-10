package com.picit.message.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "messages")
public class Message {
    @Id
    private String id;

    private String senderId;

    private String roomId;

    private String content;

    private Boolean isSeen;

    private LocalDateTime timestamp;

}
