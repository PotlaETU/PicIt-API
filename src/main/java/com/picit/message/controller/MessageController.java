package com.picit.message.controller;

import com.picit.message.controller.documentation.MessageControllerDocumentation;
import com.picit.message.dto.MessageDto;
import com.picit.message.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class MessageController implements MessageControllerDocumentation {

    private final MessageService messageService;

    @MessageMapping("chat.sendMessage")
    @SendTo("/topic/chat/{roomId}")
    public MessageDto sendMessageToConvId(@Payload MessageDto chatMessage,
                                          SimpMessageHeaderAccessor headerAccessor,
                                          @DestinationVariable("roomId") String conversationId) {
        messageService.sendMessageToConvId(chatMessage, conversationId, headerAccessor);
        return chatMessage;
    }


}
