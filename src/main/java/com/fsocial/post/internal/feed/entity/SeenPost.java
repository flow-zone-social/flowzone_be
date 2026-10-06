package com.fsocial.post.internal.feed.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tracks which posts a user has already seen.
 * Postgres không có TTL index — dọn bằng {@code SeenPostPurgeScheduler} (14 ngày).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "seen_post")
@IdClass(SeenPost.Key.class)
public class SeenPost {

    @Id
    @Column(name = "user_id", length = 36)
    String userId;

    @Id
    @Column(name = "post_id", length = 36)
    String postId;

    @Column(name = "seen_at", nullable = false)
    @Builder.Default
    LocalDateTime seenAt = LocalDateTime.now();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private String userId;
        private String postId;
    }
}
