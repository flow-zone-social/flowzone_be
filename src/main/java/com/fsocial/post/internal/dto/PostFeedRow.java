package com.fsocial.post.internal.dto;

import com.fsocial.post.internal.entity.Post;

public record PostFeedRow(
        Post post,
        String displayName,
        String avatar,
        Long likeCount,
        Long commentCount,
        Long likedByRequesterCount
) {
    public boolean isLikedByRequester() {
        return likedByRequesterCount != null && likedByRequesterCount > 0;
    }
}
