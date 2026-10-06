package com.fsocial.chat.internal.dto;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.chat.internal.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MessageDTO {
    String id;
    String conversationId;
    String senderId;
    String content;
    MessageType messageType;
    String replyToId;
    LocalDateTime createdAt;
    ActorSnapshotDTO actorSnapshotDTO;
}
