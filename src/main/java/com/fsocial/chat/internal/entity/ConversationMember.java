package com.fsocial.chat.internal.entity;

import com.fsocial.chat.internal.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import com.fsocial.shared.persistence.AbstractEntity;
import com.fsocial.user.internal.entity.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "conversation_members", uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"}))
public class ConversationMember extends AbstractEntity<String> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "role", length = 16, nullable = false)
    MemberRole role = MemberRole.MEMBER;

    @Column(name = "last_read_at")
    LocalDateTime lastReadAt;

    @Builder.Default
    @Column(name = "is_muted", nullable = false)
    boolean muted = false;

    @Builder.Default
    @Column(name = "joined_at")
    LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "left_at")
    LocalDateTime leftAt;
}
