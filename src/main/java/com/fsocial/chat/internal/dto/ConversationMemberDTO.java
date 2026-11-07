package com.fsocial.chat.internal.dto;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.chat.internal.dto.MessageDTO;
import com.fsocial.chat.internal.entity.Conversation;
import com.fsocial.chat.internal.enums.ConversationType;
import com.fsocial.chat.internal.enums.MemberRole;
import com.fsocial.chat.internal.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationMemberDTO {
    String id;
    ConversationType type;
    String name;
    String conversationAvatar;
    String userId;
    String displayName;
    String avatar;
    MessageDTO lastMessage;
    double unreadCount;

    String messageId;
    String senderId;
    String content;
    MessageType messageType;
    String replyToId;
    LocalDateTime createdAt;
}
