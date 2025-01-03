package com.picit.message.services;

import com.picit.iam.entity.User;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserRepository;
import com.picit.message.dto.MessageDto;
import com.picit.message.dto.MessageResponseDto;
import com.picit.message.dto.RoomDto;
import com.picit.message.dto.RoomRequestDto;
import com.picit.message.entity.Message;
import com.picit.message.entity.Room;
import com.picit.message.repository.MessageRepository;
import com.picit.message.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
            brokerMessagingTemplate.convertAndSend("/topic/messages" + conversationId, chatMessage);
            log.info("message sent to room {}", conversationId);
        }

        return MessageResponseDto.builder()
                .content(chatMessage.content())
                .roomId(conversationId)
                .username(userSender.getUsername())
                .createdAt(chatMessage.createdAt())
                .build();
    }

    private void getOrCreateRoom(String conversationId, Message message, User userSender) {
        if (!roomRepository.existsById(conversationId)) {
            roomRepository.save(Room.builder()
                    .id(conversationId)
                    .messages(List.of(message))
                    .users(Set.of(userSender))
                    .build());
        } else {
            var room = roomRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));
            room.getMessages().add(message);
            room.getUsers().add(userSender);
            roomRepository.save(room);
        }
    }

    public ResponseEntity<List<RoomDto>> getRoomsForUser(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));

        var rooms = roomRepository.findAll()
                .stream()
                .filter(room -> room.getUsers()
                        .stream()
                        .anyMatch(u -> u.getId().equals(user.getId())))
                .map(room -> RoomDto.builder()
                        .id(room.getId())
                        .users(room.getUsers().stream()
                                .map(User::getUsername)
                                .filter(u -> !u.equals(username))
                                .toList())
                        .type(room.getType())
                        .lastMessage(room.getMessages()
                                .stream()
                                .map(m -> MessageDto.builder()
                                        .content(m.getContent())
                                        .senderUsername(userRepository.findById(m.getSenderId())
                                                .orElseThrow(() -> new UserNotFound("User not found"))
                                                .getUsername())
                                        .createdAt(m.getTimestamp())
                                        .build())
                                .reduce((first, second) -> second)
                                .orElse(null))
                        .build())
                .toList();
        return ResponseEntity.ok(rooms);
    }

    public ResponseEntity<RoomDto> createRoom(String username, RoomRequestDto roomRequestDto) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));

        var userToChatWith = userRepository.findByUsername(roomRequestDto.username())
                .orElseThrow(() -> new UserNotFound("User not found"));

        var room = Room.builder()
                .messages(List.of())
                .users(Set.of(user, userToChatWith))
                .build();
        var roomRepositoryById = roomRepository.findById(room.getId());
        if (roomRepositoryById.isPresent()) {
            room = roomRepositoryById.get();
            return ResponseEntity.ok(
                    RoomDto.builder()
                            .id(room.getId())
                            .type(room.getType())
                            .users(room.getUsers().stream()
                                    .map(User::getUsername)
                                    .filter(u -> !u.equals(username))
                                    .toList())
                            .lastMessage(room.getMessages()
                                    .stream()
                                    .map(m -> MessageDto.builder()
                                            .content(m.getContent())
                                            .senderUsername(userRepository.findById(m.getSenderId())
                                                    .orElseThrow(() -> new UserNotFound("User not found"))
                                                    .getUsername())
                                            .createdAt(m.getTimestamp())
                                            .build())
                                    .reduce((first, second) -> second)
                                    .orElse(null))
                            .build()
            );
        }
        roomRepository.save(room);
        return ResponseEntity.ok(
                RoomDto.builder()
                        .id(room.getId())
                        .type(roomRequestDto.type())
                        .users(room.getUsers().stream()
                                .map(User::getUsername)
                                .filter(u -> !u.equals(username))
                                .toList())
                        .build()
        );
    }
}
