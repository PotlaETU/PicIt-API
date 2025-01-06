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
    private static final String USER_NOT_FOUND = "User not found";
    private static final String ROOM_NOT_FOUND = "Room not found";

    public MessageResponseDto sendMessageToConvId(MessageDto chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
        if (chatMessage == null) {
            throw new IllegalArgumentException("Chat message cannot be null");
        }
        var userSender = userRepository.findByUsername(chatMessage.senderUsername())
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        Message message = Message.builder()
                .senderId(userSender.getId())
                .roomId(conversationId)
                .content(chatMessage.content())
                .timestamp(chatMessage.createdAt())
                .isSeen(false)
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
                    .typingUsers(Set.of())
                    .build());
        } else {
            var room = roomRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException(ROOM_NOT_FOUND));
            room.getMessages().add(message);
            room.getUsers().add(userSender);
            roomRepository.save(room);
        }
    }

    public ResponseEntity<List<RoomDto>> getRoomsForUser(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        var rooms = roomRepository.findAll()
                .stream()
                .filter(room -> room.getUsers()
                        .stream()
                        .anyMatch(u -> u.getId().equals(user.getId())))
                .map(room -> toRoomDto(username, room))
                .toList();
        return ResponseEntity.ok(rooms);
    }

    public ResponseEntity<RoomDto> createRoom(String username, RoomRequestDto roomRequestDto) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        var userToChatWith = userRepository.findByUsername(roomRequestDto.username())
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        var room = Room.builder()
                .messages(List.of())
                .users(Set.of(user, userToChatWith))
                .typingUsers(Set.of())
                .build();
        var roomRepositoryById = roomRepository.findByUsers(room.getUsers());
        if (roomRepositoryById.isPresent()) {
            room = roomRepositoryById.get();
            return ResponseEntity.ok(
                    toRoomDto(username, room)
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

    public ResponseEntity<List<MessageResponseDto>> getMessagesForRoom(String roomId) {
        var messages = messageRepository.findAllByRoomId(roomId)
                .stream()
                .filter(
                        m -> roomRepository.findById(roomId)
                                .orElseThrow(() -> new IllegalArgumentException(ROOM_NOT_FOUND))
                                .getUsers()
                                .stream()
                                .anyMatch(u -> u.getId().equals(m.getSenderId())))
                .map(m -> MessageResponseDto.builder()
                        .content(m.getContent())
                        .username(userRepository.findById(m.getSenderId())
                                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND))
                                .getUsername())
                        .createdAt(m.getTimestamp())
                        .roomId(roomId)
                        .build())
                .toList();
        return ResponseEntity.ok(messages);
    }

    public void updateTypingStatus(String roomId, String username, boolean isTyping) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException(ROOM_NOT_FOUND));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        if (isTyping) {
            room.getTypingUsers()
                    .add(user);
        } else {
            room.getTypingUsers()
                    .remove(user);
        }
        roomRepository.save(room);
        brokerMessagingTemplate.convertAndSend("/topic/typing/" + roomId, room.getTypingUsers());
    }

    public void markMessageAsSeen(String messageId) {
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        message.setIsSeen(true);
        messageRepository.save(message);
    }

    private RoomDto toRoomDto(String username, Room room) {
        return RoomDto.builder()
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
                                        .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND))
                                        .getUsername())
                                .createdAt(m.getTimestamp())
                                .build())
                        .reduce((first, second) -> second)
                        .orElse(null))
                .build();
    }

}
