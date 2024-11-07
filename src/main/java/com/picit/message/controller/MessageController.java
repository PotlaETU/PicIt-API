package com.picit.message.controller;

import com.picit.message.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/api/v1/message")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;


    @MessageMapping("/chat/sendMessage/{convId}")
    public MessageService sendMessageToConvId(@Payload MessageService chatMessage, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable("convId") String conversationId) {
        messageService.sendMessageToConvId(chatMessage, conversationId, headerAccessor);
        return chatMessage;
    }


}
