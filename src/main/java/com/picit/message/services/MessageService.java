package com.picit.message.services;

import com.picit.iam.entity.User;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserRepository;
import com.picit.message.dto.MessageDto;
import com.picit.message.dto.MessageResponseDto;
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
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate brokerMessagingTemplate;
    private final UserRepository userRepository;

    public MessageResponseDto sendMessageToConvId(MessageDto chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
        if (chatMessage == null) {
            throw new IllegalArgumentException("Chat message cannot be null");
        }
          var userSender = userRepository.findByUsername(chatMessage.senderUsername())
                .orElseThrow(() -> new UserNotFound("User not found"));

        //TODO: encrypt message
        Message message = Message.builder()
                .senderId(userSender.getId())
                .roomId(conversationId)
                .content(chatMessage.content())
                .timestamp(chatMessage.createdAt())
                .build();
        getOrCreateRoom(conversationId, message, userSender);
        messageRepository.save(message);
        var sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            log.error("HeaderAccessor is null");
        } else {
            sessionAttributes.put("username", chatMessage.senderUsername());
            brokerMessagingTemplate.convertAndSend("/topic/" + conversationId, chatMessage);
        }

        return MessageResponseDto.builder()
                .content(chatMessage.content())
                .username(userSender.getUsername())
                .createdAt(chatMessage.createdAt())
                .build();
    }

    private void getOrCreateRoom(String conversationId, Message message, User userSender) {
        if (!roomRepository.existsById(conversationId)) {
            roomRepository.save(Room.builder()
                    .id(conversationId)
                    .messages(List.of(message))
                    .users(Set.of(userSender.getId()))
                    .build());
        } else {
            var room = roomRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));
            room.getMessages().add(message);
            room.getUsers().add(userSender.getId());
            roomRepository.save(room);
        }
    }
}
