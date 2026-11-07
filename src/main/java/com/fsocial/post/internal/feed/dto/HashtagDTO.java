package com.fsocial.post.internal.feed.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
public record HashtagDTO(
        String id,
        String name,
        int usageCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
