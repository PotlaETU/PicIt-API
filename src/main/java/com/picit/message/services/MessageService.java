package com.picit.message.services;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public void sendMessageToConvId(MessageService chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {

    }
}
