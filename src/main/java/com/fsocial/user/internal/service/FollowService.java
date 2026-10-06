package com.fsocial.user.internal.service;

import java.util.List;
import java.util.Set;

public interface FollowService {
    void follow(String userId, String targetId);
    void unfollow(String userId, String targetId);
    boolean isFollowing(String userId, String targetId);
    Set<String> getFollowers(String userId);
    Set<String> getFollowing(String userId);
    boolean checkFollow(String follower, String followee);
    List<String> findFolloweeIds(String userId);
}
