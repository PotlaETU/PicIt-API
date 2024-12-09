package com.picit.message.services;

import com.picit.message.dto.MessageDto;
import com.picit.message.entity.Message;
import com.picit.message.entity.Room;
import com.picit.message.repository.MessageRepository;
import com.picit.message.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    public void sendMessageToConvId(MessageDto chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
        Message message = Message.builder()
                .senderId(chatMessage.senderId())
                .roomId(conversationId)
                .content(chatMessage.content())
                .timestamp(chatMessage.createdAt())
                .build();
        messageRepository.save(message);
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.senderId());
            brokerMessagingTemplate.convertAndSend("/topic/" + conversationId, chatMessage);
        } else {
            log.error("HeaderAccessor is null");
        }
    }

    public Room getOrCreateRoom(List<String> users) {
        log.info("Creating room for users: {}", users);
        var existingRoom = roomRepository.findByUsers(users);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        } else {
            var newRoom = Room.builder()
                    .users(users)
                    .build();
            return roomRepository.save(newRoom);
        }
    }
}
