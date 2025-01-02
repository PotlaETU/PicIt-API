package com.picit.message.controller;

import com.picit.message.controller.documentation.MessageControllerDocumentation;
import com.picit.message.dto.MessageDto;
import com.picit.message.dto.MessageResponseDto;
import com.picit.message.dto.RoomDto;
import com.picit.message.dto.RoomRequestDto;
import com.picit.message.services.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class MessageController implements MessageControllerDocumentation {

    private final MessageService messageService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/messages/{roomId}")
    public MessageResponseDto sendMessageToConvId(@Payload MessageDto chatMessage,
                                                  SimpMessageHeaderAccessor headerAccessor,
                                                  @DestinationVariable String roomId) {
        log.info("Received message: {} from {} in room {}", chatMessage.content(), chatMessage.senderUsername(), roomId);
        return messageService.sendMessageToConvId(chatMessage, roomId, headerAccessor);
    }

    @GetMapping("/api/v1/messages/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsForUser(Authentication authentication) {
        return messageService.getRoomsForUser(authentication.getName());
    }

    @PostMapping("/api/v1/messages/rooms")
    public ResponseEntity<RoomDto> createRoom(Authentication authentication, @RequestBody RoomRequestDto roomRequestDto) {
        return messageService.createRoom(authentication.getName(), roomRequestDto);
    }

}
