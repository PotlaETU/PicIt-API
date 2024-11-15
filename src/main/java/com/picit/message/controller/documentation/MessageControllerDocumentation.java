package com.picit.message.controller.documentation;

import com.picit.message.dto.MessageDto;
import com.picit.message.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Tag(name = "Message", description = "Message management")
public interface MessageControllerDocumentation {

    @Operation(summary = "Send message to conversation", description = "Sends a message to a specific conversation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    MessageDto sendMessageToConvId(@Payload MessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable("convId") String conversationId);
}