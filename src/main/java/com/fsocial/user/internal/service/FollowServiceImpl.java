package com.fsocial.user.internal.service;

import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.user.internal.entity.Follow;
import com.fsocial.user.internal.repository.AccountRepository;
import com.fsocial.user.internal.repository.FollowRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class FollowServiceImpl implements FollowService {

    FollowRepository followRepository;
    AccountRepository accountRepository;

    @Override
    @Transactional
    public void follow(String userId, String targetId) {
        updateFollowRelation(userId, targetId, true);
        log.info("User {} followed user {}", userId, targetId);
    }

    @Override
    public void unfollow(String userId, String targetId) {
        updateFollowRelation(userId, targetId, false);
        log.info("User {} unfollowed user {}", userId, targetId);
    }

    @Override
    public boolean isFollowing(String userId, String targetId) {
        return followRepository.existsByFollowerIdAndFolloweeId(userId, targetId);
    }

    @Override
    public Set<String> getFollowers(String userId) {
        if (!accountRepository.existsById(userId))
            throw new AppException(StatusCode.USER_NOT_FOUND);
        return new HashSet<>(followRepository.findFollowerIds(userId));
    }

    @Override
    public Set<String> getFollowing(String userId) {
        if (!accountRepository.existsById(userId))
            throw new AppException(StatusCode.USER_NOT_FOUND);
        return new HashSet<>(followRepository.findFolloweeIds(userId));
    }

    @Override
    public boolean checkFollow(String follower, String followee) {
        accountRepository.findById(follower).orElseThrow(() -> new AppException(StatusCode.NOT_FOUND));
        accountRepository.findById(followee).orElseThrow(() -> new AppException(StatusCode.NOT_FOUND));

        return followRepository.existsByFollowerIdAndFolloweeId(follower, followee);
    }

    @Override
    public List<String> findFolloweeIds(String userId) {
        return followRepository.findFolloweeIds(userId);
    }

    /** 1 dòng trong bảng follow thay cho việc mutate 2 Set rồi save 2 document như bản Mongo. */
    private void updateFollowRelation(String userId, String targetId, boolean isFollow) {
        if (!accountRepository.existsById(targetId) || !accountRepository.existsById(userId))
            throw new AppException(StatusCode.ACCOUNT_NOT_EXISTED);

        if (isFollow) {
            // PK (follower_id, followee_id) đảm bảo idempotent
            if (!followRepository.existsByFollowerIdAndFolloweeId(userId, targetId))
                followRepository.save(new Follow(userId, targetId));
        } else {
            followRepository.deleteByFollowerIdAndFolloweeId(userId, targetId);
        }
    }
}
