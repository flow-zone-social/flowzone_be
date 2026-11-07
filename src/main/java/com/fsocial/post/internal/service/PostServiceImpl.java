package com.fsocial.post.internal.service;

import com.fsocial.post.internal.dto.*;
import com.fsocial.shared.response.ApiDataCursor;
import com.fsocial.notification.internal.dto.NotificationDTO;

import com.fsocial.user.internal.entity.Account;
import com.fsocial.post.internal.entity.Post;
import com.fsocial.post.internal.entity.PostLike;
import com.fsocial.shared.response.SearchPageResponse;
import com.fsocial.media.api.AttachmentType;
import com.fsocial.notification.api.NotificationType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.post.internal.mapper.PostMapper;
import com.fsocial.post.internal.feed.publisher.InteractionEventPublisher;
import com.fsocial.notification.api.NotificationEvent;

import com.fsocial.media.api.MediaApi;
import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.media.internal.repository.AttachmentsRepository;
import com.fsocial.post.internal.dto.PostFeedRow;
import com.fsocial.post.internal.feed.service.FeedService;
import com.fsocial.post.internal.service.PostService;
import com.fsocial.shared.redis.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fsocial.post.internal.util.PostUtils.buildPostResponse;
import com.fsocial.post.internal.util.PostUtils;
import com.fsocial.comment.internal.repository.CommentRepository;
import com.fsocial.media.api.MediaItemDTO;
import com.fsocial.post.internal.repository.PostLikeRepository;
import com.fsocial.post.internal.repository.PostRepository;
import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.user.api.UserApi;
import com.fsocial.user.internal.repository.AccountRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostServiceImpl implements PostService {
    PostRepository postRepository;
    PostLikeRepository postLikeRepository;
    AccountRepository accountRepository;
    CommentRepository commentRepository;
    AttachmentsRepository attachmentsRepository;
    UserApi userApi;
    MediaApi mediaApi;
    PostMapper postMapper;
    RedisService redisService;
    FeedService feedService;
    InteractionEventPublisher interactionEventPublisher;
    NotificationEvent notificationEvent;

    @Override
    @Transactional
    public PostDTO createPost(PostRequest postRequest) {
        MediaItemDTO[] mediaItems = mediaApi.uploadValidMedia(postRequest.getMedia(), AttachmentType.POST);
        Account owner = accountRepository.findById(postRequest.getUserId())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));
        try {
            Post post = Post.builder()
                    .owner(owner)
                    .text(postRequest.getText())
                    .html(postRequest.getHtml())
                    .createDatetime(LocalDateTime.now())
                    .tags(normalizeTags(postRequest.getTags()))
                    .build();

            Post saved = postRepository.save(post);
            linkMedia(saved, mediaItems);
            return postMapper.toPostDTO(saved);
        } catch (RuntimeException e) {
            log.error("Không thể thêm bài post vào database: {}", e.getMessage(), e);
            throw new AppException("Không thể thêm bài post vào database", StatusCode.CREATE_POST_FAILED);
        }
    }

    @Override
    @Transactional
    public PostDTO updatePost(PostUpdated post, String postId) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", StatusCode.POST_NOT_FOUND));

        existingPost.setText(post.getText());
        existingPost.setHtml(post.getHtml());
        existingPost.setUpdatedAt(LocalDateTime.now());
        return postMapper.toPostDTO(postRepository.save(existingPost));
    }

    @Override
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }

    @Override
    @Transactional
    public boolean toggleLike(String postId, String userId) throws Exception {
        boolean existed = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException("Không tìm thấy bài viết", StatusCode.POST_NOT_FOUND));
            List<String> tags = post.getTags();

            if (!existed) {
                addLike(postId, userId);
                interactionEventPublisher.publish(postId, userId, "LIKE", tags);
                notifyOwner(post.getOwner().getId(), userId, NotificationType.LIKE_SINGLE);
                return true;
            }
            removeLike(postId, userId);
            interactionEventPublisher.publish(postId, userId, "UNLIKE", tags);
            return false;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void addLike(String postId, String userId) {
        // PK (post_id, user_id) đảm bảo idempotent; existsBy đã chặn trước nên save là insert.
        postLikeRepository.save(new PostLike(postId, userId));
    }

    public void removeLike(String postId, String userId) {
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Override
    public Integer CountLike(String postId, String userId) {
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    @Transactional
    public PostDTO sharePost(PostShareRequest postRequest) {
        Post origin = postRepository.findById(postRequest.getOriginPostId())
                .orElseThrow(() -> new AppException("Không tìm thấy bài viết gốc", StatusCode.POST_NOT_FOUND));
        Account owner = accountRepository.findById(postRequest.getUserId())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));

        Post post = Post.builder()
                .owner(owner)
                .text(postRequest.getText())
                .html(postRequest.getHtml())
                .isShare(true)
                .originPostId(postRequest.getOriginPostId())
                .createDatetime(LocalDateTime.now())
                .tags(normalizeTags(origin.getTags()))
                .build();

        Post saved = postRepository.save(post);

        redisService.personalization(postRequest.getUserId(), owner.getId());

        // SHARE event → cập nhật score bài gốc + interest của user
        interactionEventPublisher.publish(postRequest.getOriginPostId(),
                postRequest.getUserId(), "SHARE", saved.getTags());
        notifyOwner(origin.getOwner().getId(), postRequest.getUserId(), NotificationType.SHARE);

        return postMapper.toPostDTO(saved);
    }

    /** Bỏ qua nếu tự thao tác trên bài viết của chính mình */
    private void notifyOwner(String ownerId, String actorId, NotificationType type) {
        if (ownerId == null || ownerId.equals(actorId)) return;
        notificationEvent.publishCreateNotification(new NotificationDTO(ownerId, actorId, type));
    }

    private void linkMedia(Post post, MediaItemDTO[] mediaItems) {
        if (mediaItems == null) return;
        for (int i = 0; i < mediaItems.length; i++) {
            MediaItemDTO item = mediaItems[i];
            if (item == null || item.getAttachmentId() == null) continue;
            post.addMedia(mediaApi.linkToPost(item.getAttachmentId(), post.getId(), i));
        }
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return List.of();
        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .distinct()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUser(String userId, String requesterId, int size) {
        return getPostsByUser(userId, requesterId, PageRequest.of(0, Math.min(50, size)));
    }

    /** owner/like/comment gộp vào 1 truy vấn (findPostsByOwner), media fetch riêng theo postIds — 2 query thay vì 4. */
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUser(String userId, String requesterId, Pageable pageable) {
        if (!userApi.canView(userId, requesterId)) return List.of();

        List<PostFeedRow> rows = postRepository.findPostsByOwner(userId, requesterId, pageable);
        if (rows.isEmpty()) return List.of();

        List<String> postIds = rows.stream().map(r -> r.post().getId()).toList();
        Map<String, List<Attachments>> mediaByPostId = attachmentsRepository.findByPostIdInOrderByOrd(postIds).stream()
                .collect(Collectors.groupingBy(a -> a.getPost().getId()));

        return rows.stream()
                .map(row -> PostUtils.buildPostResponseFromFeedRow(row, mediaByPostId.getOrDefault(row.post().getId(), List.of())))
                .toList();
    }

    @Override
    public List<PostResponse> getPostsByUserId(String userId, int feedSize) {
        List<PostResponse> feed = feedService.buildPersonalizedFeed(userId, feedSize);
        log.info("Lấy bài viết thành công, count={}", feed.size());
        return feed;
    }

    @Override
    public List<PostStatisticsLongDateDTO> countStatisticsPostLongDay(LocalDateTime startDate, LocalDateTime endDate) {
        List<PostStatisticsLongDateDTO> postStatisticsDTOS = postRepository.countByDate(startDate, endDate);
        List<PostStatisticsLongDateDTO> result = new ArrayList<>();
        LocalDateTime start = startDate.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime end = endDate.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        while (!start.equals(end)) {
            for (PostStatisticsLongDateDTO complaint : postStatisticsDTOS) {
                if (complaint.getDate().truncatedTo(ChronoUnit.DAYS).equals(start)) {
                    result.add(complaint);
                } else {
                    result.add(new PostStatisticsLongDateDTO(start, 0));
                }
            }

            if (postStatisticsDTOS.isEmpty()) {
                result.add(new PostStatisticsLongDateDTO(start, 0));
            }
            start = start.plusDays(1);
        }
        return result;
    }

    @Override
    public List<PostStatisticsDTO> countStatisticsPostToday(LocalDateTime startDate, LocalDateTime endDate) {
        List<PostStatisticsDTO> rawData = postRepository.countByCreatedAtByHours(startDate, endDate);
        Map<String, Integer> countByHour = rawData.stream()
                .collect(Collectors.toMap(PostStatisticsDTO::getHour, PostStatisticsDTO::getCount));

        List<PostStatisticsDTO> result = new ArrayList<>(24);
        for (int hour = 0; hour < 24; hour++) {
            String key = String.valueOf(hour);
            result.add(new PostStatisticsDTO(key, countByHour.getOrDefault(key, 0)));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchPageResponse<PostResponse> findByText(String text, String userId, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(50, size);
        String trimmedText = text == null ? "" : text.trim();

        if (trimmedText.isBlank()) {
            return new SearchPageResponse<>(List.of(), safePage, safeSize, false);
        }

        Pageable pageable = PageRequest.of(safePage, safeSize + 1);
        List<Post> posts = postRepository.findByTextContainingIgnoreCase(trimmedText, pageable);
        boolean hasMore = posts.size() > safeSize;
        List<PostResponse> items = toPostResponses(posts.stream().limit(safeSize).toList(), userId);

        return new SearchPageResponse<>(items, safePage, safeSize, hasMore);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin bài viết", StatusCode.POST_NOT_FOUND));
        ActorSnapshotDTO owner = userApi.getOwner(post.getOwner().getId());
        Integer commentCount = commentRepository.countByPostId(postId);
        return buildPostResponse(post, owner,
                commentCount == null ? 0 : commentCount,
                postLikeRepository.countByPostId(postId),
                postLikeRepository.existsByPostIdAndUserId(postId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostByFollowing(String userId) {
        Pageable pageable = PageRequest.of(0, 10);

        List<String> followingIds = userApi.findFolloweeIds(userId);
        if (followingIds.isEmpty()) {
            return List.of();
        }

        List<String> viewed = redisService.getViewedFollowing(userId);
        List<Post> result = postRepository.findByOwnerIdInAndIdNotInOrderByCreateDatetimeDesc(
                followingIds, excludable(viewed), pageable);

        result.forEach(p -> redisService.viewedFollowing(userId, p.getId()));
        return toPostResponses(result, userId);
    }

    /** JPQL {@code not in ()} rỗng là cú pháp lỗi — luôn có ít nhất 1 phần tử không tồn tại. */
    private List<String> excludable(List<String> ids) {
        return (ids == null || ids.isEmpty()) ? List.of("") : ids;
    }

    private List<PostResponse> toPostResponses(List<Post> posts, String requesterId) {
        if (posts.isEmpty()) return List.of();

        List<String> postIds = posts.stream().map(Post::getId).toList();
        List<String> ownerIds = posts.stream()
                .map(p -> p.getOwner().getId())
                .distinct().toList();

        Map<String, ActorSnapshotDTO> accountMap = userApi.getOwners(ownerIds).stream()
                .collect(Collectors.toMap(ActorSnapshotDTO::getUserId, Function.identity()));

        Map<String, Integer> commentCountMap = commentRepository.countByPostIdIn(postIds).stream()
                .collect(Collectors.toMap(CommentRepository.PostCommentCount::_id,
                        CommentRepository.PostCommentCount::count));

        // 1 truy vấn thay vì 2 (count + liked-by-requester) — cùng đọc bảng post_like.
        Map<String, Integer> likeCountMap = new HashMap<>();
        Set<String> likedByRequester = new HashSet<>();
        for (Object[] row : postLikeRepository.countAndLikedByPostIdIn(requesterId, postIds)) {
            String postId = (String) row[0];
            likeCountMap.put(postId, ((Number) row[1]).intValue());
            if (((Number) row[2]).longValue() > 0) {
                likedByRequester.add(postId);
            }
        }

        return posts.stream()
                .map(post -> {
                    String ownerId = post.getOwner().getId();
                    ActorSnapshotDTO owner = accountMap.get(ownerId);
                    if (owner == null) {
                        log.warn("Bỏ qua post {} vì không tìm thấy account {}", post.getId(), ownerId);
                        return null;
                    }
                    return buildPostResponse(post, owner,
                            commentCountMap.getOrDefault(post.getId(), 0),
                            likeCountMap.getOrDefault(post.getId(), 0),
                            likedByRequester.contains(post.getId()));
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public ApiDataCursor<PostResponse> getMyPost(String userId, String lastItem, String createdAt) {

        Sort sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
        ScrollPosition position;
        if(lastItem.isEmpty()) {
            position = ScrollPosition.keyset();
        }else {
            position = ScrollPosition.forward(Map.of("createdAt", createdAt, "id", lastItem));
        }

        Window<List<Post>> data = postRepository.findByOwnerId(userId, sort, position);



        return (ApiDataCursor<PostResponse>) List.of();
    }
}
