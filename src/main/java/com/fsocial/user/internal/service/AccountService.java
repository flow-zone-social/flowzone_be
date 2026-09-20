package com.fsocial.user.internal.service;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.shared.response.ApiResponse;
import com.fsocial.user.internal.dto.AccountRegisterRequest;
import com.fsocial.user.internal.dto.DuplicationRequest;
import com.fsocial.user.internal.dto.AccountResponse;
import com.fsocial.user.internal.dto.AccountStatisticRegisterDTO;
import com.fsocial.user.internal.dto.AccountStatisticRegisterLongDateDTO;
import com.fsocial.user.internal.dto.DuplicationResponse;
import com.fsocial.user.internal.dto.ManageUserResponse;
import com.fsocial.shared.response.SearchPageResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface AccountService {
    AccountResponse persistAccount(AccountRegisterRequest request);
    AccountResponse getUser(String id);
    void resetPassword(String email, String newPassword);
    ApiResponse<DuplicationResponse> checkDuplication(DuplicationRequest request);
    void changePassword(String userId, String oldPassword, String newPassword);
    boolean existsById(String id);
    List<AccountStatisticRegisterDTO> countByCreatedAtByHours(LocalDateTime startDay, LocalDateTime endDay);
    List<AccountStatisticRegisterLongDateDTO> countByCreatedAtByStartEnd(LocalDateTime startDay, LocalDateTime endDay);
    String banUser(String userId);
    AccountResponse getProfile (String userId);
    ActorSnapshotDTO getOwner(String userId);
    void follow(String userId, String targetId);
    void unfollow(String userId, String targetId);
    boolean isFollowing(String userId, String targetId);
    Set<String> getFollowers(String userId);
    Set<String> getFollowing(String userId);
    List<ManageUserResponse> getAllUsers();
    SearchPageResponse<AccountResponse> searchUsers(String keyword, int page, int size);
}
