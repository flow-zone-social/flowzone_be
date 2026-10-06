package com.fsocial.chat.internal.service;

import com.fsocial.chat.internal.dto.*;

import java.util.List;
import java.util.Map;
import com.fsocial.chat.internal.dto.ConversationDTO;
import com.fsocial.chat.internal.dto.CreateConversationRequest;
import com.fsocial.chat.internal.dto.MarkReadDTO;
import com.fsocial.chat.internal.dto.MessageDTO;
import com.fsocial.chat.internal.dto.SendMessageRequest;

public interface ChatService {

    List<ConversationDTO> getConversationsForUser(String userId);

    ConversationDTO createConversation(String userId, CreateConversationRequest request);

    List<MessageDTO> getMessages(String conversationId, String userId, String cursor);

    MessageDTO sendMessage(String userId, SendMessageRequest request);

    List<String> getMemberIds(String conversationId);

    void markReadConversation(MarkReadDTO request, String userId);

    Map<String, Boolean> listConversationOnline(List<String> userId);
}
