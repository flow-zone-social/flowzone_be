package com.fsocial.post.internal.feed.service;

import com.fsocial.post.internal.feed.dto.HashtagDTO;
import com.fsocial.post.internal.feed.dto.HashtagRequest;
import com.fsocial.post.internal.feed.dto.HashtagSuggestion;

import java.util.List;

public interface HashtagService {
    List<HashtagSuggestion> findHastagByName(String name);

    List<HashtagDTO> getAllHashtags();

    HashtagDTO getHashtagById(String id);

    HashtagDTO createHashtag(HashtagRequest request);

    HashtagDTO updateHashtag(String id, HashtagRequest request);

    void deleteHashtag(String id);

    HashtagDTO restoreHashtag(String id);

    void useHashtag(String hashtagName, String postId);
}
