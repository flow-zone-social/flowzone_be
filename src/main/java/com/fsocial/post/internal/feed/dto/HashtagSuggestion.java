package com.fsocial.post.internal.feed.dto;

import lombok.*;

@Builder
public record HashtagSuggestion(
        String id,
        String name,
        int usageCount
) {
}
