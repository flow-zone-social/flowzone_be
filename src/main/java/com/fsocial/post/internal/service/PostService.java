package com.fsocial.post.internal.service;

import com.fsocial.post.internal.dto.*;
import com.fsocial.shared.response.ApiDataCursor;

import com.fsocial.shared.response.SearchPageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    PostDTO createPost(PostRequest request);
    PostDTO updatePost(PostUpdated post, String postId);
    void deletePost(String postId) ;
    boolean toggleLike(String postId, String userId) throws Exception;
    Integer CountLike(String postId, String userId);
    PostDTO sharePost (PostShareRequest dto );

    // Methods from timelineService
    List<PostResponse> getPostsByUserId(String userId, int feedSize);

//    com.fsocial.postservice.dto.profile.ProfileResponse getProfile(String id);

    SearchPageResponse<PostResponse> findByText(String text, String userId, int page, int size);

    PostResponse getPostById(String postId, String userId);

    List<PostStatisticsDTO> countStatisticsPostToday(LocalDateTime startDate, LocalDateTime endDate);

    List<PostStatisticsLongDateDTO> countStatisticsPostLongDay(LocalDateTime startDate, LocalDateTime endDate);

    List<PostResponse> getPostByFollowing(String userId);

    ApiDataCursor<PostResponse> getMyPost(String userId, String lastItem, String createdAt);
}
