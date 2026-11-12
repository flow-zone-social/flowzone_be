package com.fsocial.user.api;

import com.fsocial.user.internal.dto.AccountResponse;
import com.fsocial.user.internal.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserApi {

    ActorSnapshotDTO getOwner(String userId);

    /** Batch lookup — tránh N+1 khi build danh sách post/comment/thông báo. */
    List<ActorSnapshotDTO> getOwners(List<String> userIds);

    boolean existsById(String id);

    boolean isFollowing(String followerId, String followeeId);

    List<String> findFolloweeIds(String userId);

    /** owner public, chính chủ, hoặc requester đang follow owner thì mới xem được. */
    boolean canView(String ownerId, String requesterId);

    User findById(String id);
}
