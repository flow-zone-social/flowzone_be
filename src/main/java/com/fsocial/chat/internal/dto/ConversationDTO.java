package com.fsocial.chat.internal.dto;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.chat.internal.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ConversationDTO {
    String id;
    ConversationType type;
    String name;
    String avatarUrl;
    List<ActorSnapshotDTO> members;
    MessageDTO lastMessage;
    double unreadCount;
}
