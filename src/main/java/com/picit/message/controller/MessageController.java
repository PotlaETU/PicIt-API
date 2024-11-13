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

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public MessageDto sendMessage(@Payload MessageDto chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/sendMessage/{convId}")
    @SendTo("/topic/{convId}")
    public MessageService sendMessageToConvId(@Payload MessageService chatMessage, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable("convId") String conversationId) {
        messageService.sendMessageToConvId(chatMessage, conversationId, headerAccessor);
        return chatMessage;
    }


}
