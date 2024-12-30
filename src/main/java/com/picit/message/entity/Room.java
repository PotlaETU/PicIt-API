package com.picit.message.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Document(collection = "room")
public class Room {
    @Id
    private String id;

    private Set<String> users;

    private List<Message> messages;
}
