package com.fsocial.chat.internal.service;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.user.api.UserApi;
import com.fsocial.chat.internal.dto.ConversationMemberDTO;
import com.fsocial.chat.internal.dto.*;
import com.fsocial.chat.internal.entity.Conversation;
import com.fsocial.chat.internal.entity.ConversationMember;
import com.fsocial.chat.internal.entity.Message;
import com.fsocial.chat.internal.enums.ConversationType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.chat.internal.mapper.ChatMapper;
import com.fsocial.user.internal.repository.UserRepository;
import com.fsocial.chat.internal.repository.ConversationMemberRepository;
import com.fsocial.chat.internal.repository.ConversationRepository;
import com.fsocial.chat.internal.repository.MessageRepository;
import com.fsocial.chat.internal.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fsocial.chat.internal.dto.ConversationDTO;
import com.fsocial.chat.internal.dto.CreateConversationRequest;
import com.fsocial.chat.internal.dto.MarkReadDTO;
import com.fsocial.chat.internal.dto.MessageDTO;
import com.fsocial.chat.internal.dto.SendMessageRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {

    static final int PAGE_SIZE = 20;
    static final String CURSOR_SEP = "_";
    static final LocalDateTime EPOCH = LocalDateTime.of(1970, 1, 1, 0, 0);

    ConversationRepository conversationRepository;
    ConversationMemberRepository conversationMemberRepository;
    MessageRepository messageRepository;
    UserRepository userRepository;
    UserApi userApi;
    ChatMapper chatMapper;
    private final OnlineServiceImpl onlineServiceImpl;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDTO> getConversationsForUser(String userId) {
        List<ConversationMemberDTO> rows = conversationMemberRepository.findConversationAndUnreadCount(userId, EPOCH);
        OnlineServiceImpl onlineService;
        Map<String, ConversationDTO> byConversationId = new LinkedHashMap<>();
        for (ConversationMemberDTO row : rows) {
            ConversationDTO dto = byConversationId.computeIfAbsent(row.getId(), id -> {

                return ConversationDTO.builder()
                        .id(row.getId())
                        .type(row.getType())
                        .name(row.getName())
                        .avatarUrl(row.getConversationAvatar())
                        .members(new java.util.ArrayList<>())
                        .lastMessage(row.getMessageId() == null ? null : MessageDTO.builder()
                                                                         .id(row.getMessageId())
                                                                         .conversationId(row.getId())
                                                                         .senderId(row.getSenderId())
                                                                         .content(row.getContent())
                                                                         .messageType(row.getMessageType())
                                                                         .replyToId(row.getReplyToId())
                                                                         .createdAt(row.getCreatedAt())
                                                                         .build())
                        .unreadCount(row.getUnreadCount())
                        .build();
                    }

            );

            dto.getMembers().add(ActorSnapshotDTO.builder()
                    .userId(row.getUserId())
                    .displayName(row.getDisplayName())
                    .avatar(row.getAvatar())
                    .build());
        }

        return List.copyOf(byConversationId.values());
    }

    private ConversationDTO toConversationDTOWithDetails(Conversation conversation) {
        ConversationDTO dto = chatMapper.toConversationDTO(conversation);

        List<String> memberIds = conversationMemberRepository.findUserIdsByConversationId(conversation.getId());
        dto.setMembers(userApi.getOwners(memberIds));

        Message last = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId());
        dto.setLastMessage(last == null ? null : chatMapper.toMessageDTO(last));

        return dto;
    }

    @Override
    @Transactional
    public ConversationDTO createConversation(String userId, CreateConversationRequest request) {
        List<String> memberIds = request.getMemberIds();
        if (memberIds == null || memberIds.isEmpty()) {
            throw new AppException("Member list must not be empty", StatusCode.PARAMATER_NOT_FOUND);
        }

        ConversationType type = request.getType() == null ? ConversationType.DIRECT : request.getType();

        if (type == ConversationType.DIRECT) {
            String otherUserId = memberIds.get(0);
            List<String> pair = List.of(userId, otherUserId);
            List<String> existing = conversationMemberRepository.findConversationIdsByExactMembers(pair, 2);
            for (String conversationId : existing) {
                Conversation candidate = conversationRepository.findById(conversationId).orElse(null);
                if (candidate != null && candidate.getType() == ConversationType.DIRECT) {
                    return toConversationDTOWithDetails(candidate);
                }
            }
        }

        Conversation conversation = conversationRepository.save(Conversation.builder()
                .type(type)
                .name(type == ConversationType.GROUP ? request.getName() : null)
                .build());

        List<String> allMemberIds = java.util.stream.Stream.concat(
                        java.util.stream.Stream.of(userId), memberIds.stream())
                .distinct()
                .toList();

        for (String memberId : allMemberIds) {
            conversationMemberRepository.save(ConversationMember.builder()
                    .conversation(conversation)
                    .user(userRepository.getReferenceById(memberId))
                    .role(memberId.equals(userId) ? com.fsocial.chat.internal.enums.MemberRole.OWNER : com.fsocial.chat.internal.enums.MemberRole.MEMBER)
                    .build());
        }

        return toConversationDTOWithDetails(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessages(String conversationId, String userId, String cursor) {
        requireMember(conversationId, userId);

        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        List<Message> messages = (cursor == null || cursor.isBlank())
                ? messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                : findAfterCursor(conversationId, cursor, pageable);

        List<String> senderIds = messages.stream().map(m -> m.getSender().getId()).distinct().toList();
        Map<String, ActorSnapshotDTO> senderById = userApi.getOwners(senderIds).stream()
                .collect(java.util.stream.Collectors.toMap(ActorSnapshotDTO::getUserId, a -> a));

        return messages.stream()
                .map(m -> {
                    MessageDTO dto = chatMapper.toMessageDTO(m);
                    dto.setActorSnapshotDTO(senderById.get(dto.getSenderId()));
                    return dto;
                })
                .toList();
    }

    private List<Message> findAfterCursor(String conversationId, String cursor, Pageable pageable) {
        int sep = cursor.lastIndexOf(CURSOR_SEP);
        if (sep <= 0) return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
        try {
            LocalDateTime before = LocalDateTime.ofEpochSecond(
                    Long.parseLong(cursor.substring(0, sep)) / 1000, 0, ZoneOffset.UTC);
            return messageRepository.findByConversationIdAndCreatedAtBeforeOrderByCreatedAtDesc(conversationId, before, pageable);
        } catch (NumberFormatException e) {
            return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
        }
    }

    @Override
    @Transactional
    public MessageDTO sendMessage(String userId, SendMessageRequest request) {
        requireMember(request.getConversationId(), userId);

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new AppException(StatusCode.MESSAGE_SEND_FAILED);
        }

        Conversation conversationRef = conversationRepository.getReferenceById(request.getConversationId());
        Message replyTo = request.getReplyToId() == null ? null : messageRepository.getReferenceById(request.getReplyToId());
        Message message = messageRepository.save(Message.builder()
                .conversation(conversationRef)
                .sender(userRepository.getReferenceById(userId))
                .content(request.getContent())
                .messageType(request.getMessageType() == null ? com.fsocial.chat.internal.enums.MessageType.TEXT : request.getMessageType())
                .replyTo(replyTo)
                .build());

        MessageDTO dto = chatMapper.toMessageDTO(message);
        dto.setActorSnapshotDTO(ActorSnapshotDTO.builder()
                        .userId(message.getSender().getId())
                        .displayName(message.getSender().getDisplayName())
                        .avatar(message.getSender().getAvatar())
                .build());
        return dto;
    }

    @Override
    public List<String> getMemberIds(String conversationId) {
        return conversationMemberRepository.findUserIdsByConversationId(conversationId);
    }

    @Override
    public void markReadConversation(MarkReadDTO request, String userId) {
        ConversationMember member =
                conversationMemberRepository.findByConversationIdAndUserId(request.getConversationId(), userId)
                        .orElseThrow(() -> new AppException(StatusCode.NOT_CONVERSATION_MEMBER));
        member.setLastReadAt(LocalDateTime.now());
        conversationMemberRepository.save(member);
    }

    @Override
    public Map<String, Boolean> listConversationOnline(List<String> userId) {

        return onlineServiceImpl.isOnlineBatch(userId);
    }

    private void requireMember(String conversationId, String userId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(StatusCode.CONVERSATION_NOT_FOUND);
        }
        if (!conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new AppException(StatusCode.NOT_CONVERSATION_MEMBER);
        }
    }
}
