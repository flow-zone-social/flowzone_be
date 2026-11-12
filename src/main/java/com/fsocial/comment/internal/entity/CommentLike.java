package com.fsocial.comment.internal.entity;

import com.fsocial.user.internal.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comment_like")
@IdClass(CommentLike.Key.class)
public class CommentLike {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    Comment comment;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        // Tên field phải trùng với @Id ở entity; kiểu là kiểu PK của Comment/User.
        private String comment;
        private String user;
    }
}
