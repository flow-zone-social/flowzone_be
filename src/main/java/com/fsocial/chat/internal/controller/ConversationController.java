package com.fsocial.chat.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.chat.internal.dto.ConversationDTO;
import com.fsocial.chat.internal.dto.CreateConversationRequest;
import com.fsocial.chat.internal.dto.MessageDTO;
import com.fsocial.chat.internal.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/conversations")
public class ConversationController {

    ChatService chatService;

    @GetMapping
    public ApiResponse<List<ConversationDTO>> getConversations(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<List<ConversationDTO>>builder()
                .data(chatService.getConversationsForUser(jwt.getSubject()))
                .message("Get conversations success")
                .build();
    }

    @PostMapping
    public ApiResponse<ConversationDTO> createConversation(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody CreateConversationRequest request) {
        return ApiResponse.<ConversationDTO>builder()
                .data(chatService.createConversation(jwt.getSubject(), request))
                .message("Create conversation success")
                .build();
    }

    @GetMapping("/{conversationId}/messages")
    public ApiResponse<List<MessageDTO>> getMessages(@AuthenticationPrincipal Jwt jwt,
                                                       @PathVariable String conversationId,
                                                       @RequestParam(required = false) String cursor) {
        return ApiResponse.<List<MessageDTO>>builder()
                .data(chatService.getMessages(conversationId, jwt.getSubject(), cursor))
                .message("Get messages success")
                .build();
    }
}
