package com.fsocial.post.internal.feed.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Thay cho {@code UserInterests.interests[]} embedded của Mongo — mỗi tag một dòng,
 * bỏ pattern upsert-rồi-positional-inc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_interest")
@IdClass(UserInterest.Key.class)
public class UserInterest {

    @Id
    @Column(name = "user_id", length = 36)
    String userId;

    @Id
    @Column(name = "tag", length = 64)
    String tag;

    @Column(name = "weight", nullable = false)
    double weight;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    LocalDateTime updatedAt = LocalDateTime.now();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private String userId;
        private String tag;
    }
}
