package com.fsocial.chat.internal.repository;

import com.fsocial.chat.internal.dto.ConversationMemberDTO;
import com.fsocial.chat.internal.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, String> {

    Optional<ConversationMember> findByConversationIdAndUserId(String conversationId, String userId);

    boolean existsByConversationIdAndUserId(String conversationId, String userId);

    List<ConversationMember> findByConversationId(String conversationId);

    @Query("select m.conversation.id from ConversationMember m where m.user.id = :userId")
    List<String> findConversationIdsByUserId(@Param("userId") String userId);

    @Query("select m.user.id from ConversationMember m where m.conversation.id = :conversationId")
    List<String> findUserIdsByConversationId(@Param("conversationId") String conversationId);

    @Query("""
            select m.conversation.id from ConversationMember m
            where m.user.id in :userIds
            group by m.conversation.id
            having count(distinct m.user.id) = :memberCount
            """)
    List<String> findConversationIdsByExactMembers(
            @Param("userIds") List<String> userIds, @Param("memberCount") long memberCount);

    @Query("""
            SELECT new com.fsocial.chat.internal.dto.ConversationMemberDTO(
                c.id, c.type, c.name, c.avatarUrl,
                mem.user.id, ac.displayName, ac.avatar,
                null,
                (SELECT COUNT(m2) FROM Message m2
                    WHERE m2.conversation = c
                      AND m2.sender.id <> :senderId
                      AND m2.createdAt > COALESCE(cm.lastReadAt, :epoch)),
                lm.id, lm.sender.id, lm.content, lm.messageType, lm.replyTo.id, lm.createdAt
            )
            FROM ConversationMember cm
            JOIN cm.conversation c
            JOIN ConversationMember mem ON mem.conversation = c AND mem.leftAt IS NULL
            JOIN User ac ON ac.id = mem.user.id
            LEFT JOIN Message lm ON lm.conversation = c
                AND lm.createdAt = (SELECT MAX(m3.createdAt) FROM Message m3 WHERE m3.conversation = c)
            WHERE cm.user.id = :senderId
              AND cm.leftAt IS NULL
            ORDER BY lm.createdAt DESC NULLS LAST
            """)
    List<ConversationMemberDTO> findConversationAndUnreadCount(
            @Param("senderId") String senderId, @Param("epoch") java.time.LocalDateTime epoch);
}
