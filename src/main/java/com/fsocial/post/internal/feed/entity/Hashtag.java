package com.fsocial.post.internal.feed.entity;

import com.fsocial.shared.persistence.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Catalog hashtag, đồng bộ định kỳ từ post.tags qua HashtagSyncScheduler
 * (không upsert lúc tạo/share post để tránh thêm write-path overhead).
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hashtag")
public class Hashtag extends AbstractEntity<String> {

    @Column(name = "name", length = 64, nullable = false, unique = true)
    String name;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    int usageCount = 0;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    boolean deleted = false;
}
