package com.fsocial.post.internal.feed.service;

import com.fsocial.post.internal.feed.dto.HashtagDTO;
import com.fsocial.post.internal.feed.dto.HashtagRequest;
import com.fsocial.post.internal.feed.dto.HashtagSuggestion;
import com.fsocial.post.internal.feed.entity.Hashtag;
import com.fsocial.post.internal.feed.mapper.HashtagMapper;
import com.fsocial.post.internal.feed.repository.HashtagRepository;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HashtagServiceImpl implements HashtagService {

    HashtagRepository hashtagRepository;
    HashtagMapper hashtagMapper;
    RedisTemplate redisTemplate;

    @Override
    public List<HashtagSuggestion> findHastagByName(String name) {
        return hashtagRepository.findSuggestionByName(name);
    }

    @Override
    public List<HashtagDTO> getAllHashtags() {
        return hashtagMapper.toListDTO(hashtagRepository.findAllByDeletedFalseOrderByUsageCountDesc());
    }

    @Override
    public HashtagDTO getHashtagById(String id) {
        return hashtagMapper.toDTO(findActiveOrThrow(id));
    }

    @Override
    public HashtagDTO createHashtag(HashtagRequest request) {
        Hashtag existing = hashtagRepository.findByNameIgnoreCase(request.getName()).orElse(null);

        if (existing != null) {
            if (!existing.isDeleted()) {
                throw new AppException(StatusCode.HASHTAG_ALREADY_EXISTS);
            }
            existing.setDeleted(false);
            existing.setUpdatedAt(LocalDateTime.now());
            return hashtagMapper.toDTO(hashtagRepository.save(existing));
        }

        Hashtag hashtag = hashtagMapper.toEntity(request);
        return hashtagMapper.toDTO(hashtagRepository.save(hashtag));
    }

    @Override
    public HashtagDTO updateHashtag(String id, HashtagRequest request) {
        Hashtag hashtag = findActiveOrThrow(id);

        hashtagRepository.findByNameIgnoreCase(request.getName())
                .filter(other -> !other.getId().equals(id) && !other.isDeleted())
                .ifPresent(other -> {
                    throw new AppException(StatusCode.HASHTAG_ALREADY_EXISTS);
                });

        hashtag.setName(request.getName());
        hashtag.setUpdatedAt(LocalDateTime.now());
        return hashtagMapper.toDTO(hashtagRepository.save(hashtag));
    }

    @Override
    public void deleteHashtag(String id) {
        Hashtag hashtag = findActiveOrThrow(id);
        hashtag.setDeleted(true);
        hashtag.setUpdatedAt(LocalDateTime.now());
        hashtagRepository.save(hashtag);
    }

    @Override
    public HashtagDTO restoreHashtag(String id) {
        Hashtag hashtag = hashtagRepository.findById(id)
                .orElseThrow(() -> new AppException(StatusCode.HASHTAG_NOT_FOUND));
        hashtag.setDeleted(false);
        hashtag.setUpdatedAt(LocalDateTime.now());
        return hashtagMapper.toDTO(hashtagRepository.save(hashtag));
    }

    private Hashtag findActiveOrThrow(String id) {
        return hashtagRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(StatusCode.HASHTAG_NOT_FOUND));
    }

    @Override
    public void useHashtag(String hashtagName, String postId) {

    }
}
