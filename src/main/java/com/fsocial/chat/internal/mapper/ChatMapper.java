package com.fsocial.chat.internal.mapper;

import com.fsocial.chat.internal.dto.ConversationDTO;
import com.fsocial.chat.internal.dto.MessageDTO;
import com.fsocial.chat.internal.entity.Conversation;
import com.fsocial.chat.internal.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fsocial.post.internal.mapper.PostMapper;

/**
 * Members / lastMessage không map ở đây — service tự build vì cần batch-resolve
 * qua UserRepository/MessageRepository (tránh N+1), giống PostMapper.
 */
@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "replyToId", source = "replyTo.id")
//    @Mapping(target ="actorSnapshotDTO.userId", source = "message.")
    MessageDTO toMessageDTO(Message message);

    ConversationDTO toConversationDTO(Conversation conversation);
}
