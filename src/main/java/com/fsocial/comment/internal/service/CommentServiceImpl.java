package com.fsocial.comment.internal.service;

import com.fsocial.comment.internal.dto.CommentDTORequest;
import com.fsocial.comment.internal.dto.CommentResponse;
import com.fsocial.comment.internal.dto.CommentUpdateDTORequest;
import com.fsocial.notification.internal.dto.NotificationDTO;
import com.fsocial.post.internal.dto.ContentResponse;
import com.fsocial.media.api.MediaItemDTO;
import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.user.api.UserApi;
import com.fsocial.comment.internal.entity.Comment;
import com.fsocial.comment.internal.entity.CommentLike;
import com.fsocial.comment.internal.entity.CommentMedia;
import com.fsocial.post.internal.entity.Post;
import com.fsocial.media.api.MediaType;
import com.fsocial.notification.api.NotificationType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.post.internal.feed.publisher.InteractionEventPublisher;
import com.fsocial.notification.api.NotificationEvent;
import com.fsocial.comment.internal.repository.CommentLikeRepository;
import com.fsocial.comment.internal.repository.CommentRepository;
import com.fsocial.post.internal.repository.PostRepository;
import com.fsocial.comment.internal.service.CommentService;
import com.fsocial.shared.redis.RedisService;
import com.fsocial.media.api.MediaApi;
import com.fsocial.post.internal.util.PostUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;
    CommentLikeRepository commentLikeRepository;
    MediaApi mediaApi;
    PostRepository postRepository;
    UserApi userApi;
    RedisService redisService;
    InteractionEventPublisher interactionEventPublisher;
    NotificationEvent notificationEvent;

    @Override
    @Transactional
    public CommentResponse addComment(CommentDTORequest request) {
        MediaItemDTO[] mediaItems = mediaApi.uploadValidMedia(request.getMedia());

        String postId = request.getPostId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Không tìm thấy bài đăng", StatusCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .post(post)
                .userId(request.getUserId())
                .text(request.getText())
                .html(request.getHtml())
                .createDatetime(LocalDateTime.now())
                .build();
        attachMedia(comment, mediaItems);
        Comment saved = commentRepository.save(comment);

        String ownerId = post.getOwner().getId();
        redisService.personalization(saved.getUserId(), ownerId);

        // Publish async COMMENT event for score + interest update
        interactionEventPublisher.publish(postId, request.getUserId(), "COMMENT", post.getTags());

        notifyOwner(ownerId, request.getUserId(), NotificationType.COMMENT_SINGLE);

        ActorSnapshotDTO owner = userApi.getOwners(List.of(saved.getUserId())).stream().findFirst().orElse(null);
        return buildCommentResponse(saved, owner, 0, 0, false);
    }

    /** Bỏ qua nếu tự bình luận trên bài viết của chính mình */
    private void notifyOwner(String ownerId, String actorId, NotificationType type) {
        if (ownerId == null || ownerId.equals(actorId)) return;
        notificationEvent.publishCreateNotification(new NotificationDTO(ownerId, actorId, type));
    }

    private void attachMedia(Comment comment, MediaItemDTO[] mediaItems) {
        if (mediaItems == null) return;
        for (MediaItemDTO item : mediaItems) {
            if (item == null) continue;
            comment.addMedia(CommentMedia.builder()
                    .url(item.getUrl())
                    .type(MediaType.fromValue(item.getType()))
                    .width(item.getWidth())
                    .height(item.getHeight())
                    .build());
        }
    }

    @Override
    @Transactional
    public boolean toggleLikeComment(String commentId, String userId) {
        if (!commentExist(commentId))
            throw new AppException("Bình luận không tồn tại", StatusCode.COMMENT_NOT_FOUND);
        if (!userExists(userId))
            throw new AppException("Tài khoản người dùng không tồn tại", StatusCode.USER_NOT_FOUND);

        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            // PK (comment_id, user_id) đảm bảo idempotent
            commentLikeRepository.save(new CommentLike(commentId, userId));
            return true;
        }
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        return false;
    }

    @Override
    public Integer countLike(String commentId, String userId) {
        return commentLikeRepository.countByCommentId(commentId);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(CommentUpdateDTORequest request) {
        if (!userExists(request.getUserId()))
            throw new AppException("User không tồn tại", StatusCode.USER_NOT_FOUND);

        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new AppException("Không tìm thấy comment", StatusCode.COMMENT_NOT_FOUND));
        comment.setText(request.getText());
        comment.setHtml(request.getHtml());
        return convertToCommentResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public String deleteComment(String commentID) {
        commentRepository.findById(commentID).ifPresent(comment -> {
            // FK comment.parent_id ON DELETE CASCADE dọn reply; comment_like/comment_media cascade theo
            commentRepository.deleteById(commentID);
            // Publish COMMENT_DELETE so score + interest are recomputed
            interactionEventPublisher.publish(comment.getPost().getId(), comment.getUserId(), "COMMENT_DELETE",
                    postRepository.findById(comment.getPost().getId()).map(Post::getTags).orElse(List.of()));
        });
        return "Xóa comment thành công";
    }

    public boolean userExists(String userId) {
        return userApi.existsById(userId);
    }

    public boolean commentExist(String commentId) {
        return commentRepository.existsById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(String postId) {
        return toCommentResponses(commentRepository.findByPostId(postId), currentUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse convertToCommentResponse(Comment comment) {
        ActorSnapshotDTO owner = userApi.getOwners(List.of(comment.getUserId())).stream().findFirst().orElse(null);
        String requesterId = currentUserId();
        return buildCommentResponse(comment, owner,
                commentLikeRepository.countByCommentId(comment.getId()),
                commentRepository.findByParentId(comment.getId()).size(),
                requesterId != null
                        && commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), requesterId));
    }

    private List<CommentResponse> toCommentResponses(List<Comment> comments, String requesterId) {
        if (comments.isEmpty()) return List.of();

        List<String> commentIds = comments.stream().map(Comment::getId).toList();
        List<String> ownerIds = comments.stream().map(Comment::getUserId).distinct().toList();

        Map<String, ActorSnapshotDTO> accountMap = userApi.getOwners(ownerIds).stream()
                .collect(Collectors.toMap(ActorSnapshotDTO::getUserId, Function.identity()));

        Map<String, Integer> likeCountMap = commentLikeRepository.countByCommentIdIn(commentIds).stream()
                .collect(Collectors.toMap(r -> (String) r[0], r -> ((Number) r[1]).intValue()));

        Map<String, Integer> replyCountMap = commentRepository.countByParentIdIn(commentIds).stream()
                .collect(Collectors.toMap(r -> (String) r[0], r -> ((Number) r[1]).intValue()));

        Set<String> liked = requesterId == null
                ? Set.of()
                : new HashSet<>(commentLikeRepository.findLikedCommentIds(requesterId, commentIds));

        return comments.stream()
                .map(c -> buildCommentResponse(c, accountMap.get(c.getUserId()),
                        likeCountMap.getOrDefault(c.getId(), 0),
                        replyCountMap.getOrDefault(c.getId(), 0),
                        liked.contains(c.getId())))
                .collect(Collectors.toList());
    }

    private CommentResponse buildCommentResponse(Comment comment, ActorSnapshotDTO owner,
                                                 int likeCount, int replyCount, boolean liked) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(buildContent(comment))
                .countLikes(likeCount)
                .countReplies(replyCount)
                .displayName(owner == null ? "" : owner.getDisplayName())
                .userId(comment.getUserId())
                .reply(comment.isReply())
                .like(liked)
                .avatar(owner == null ? null : owner.getAvatar())
                .createDatetime(comment.getCreateDatetime())
                .build();
    }

    static ContentResponse buildContent(Comment comment) {
        List<CommentMedia> media = comment.getMedia();
        return ContentResponse.builder()
                .text(comment.getText())
                .html(comment.getHtml())
                .media(media == null ? List.of() : media.stream()
                        .filter(Objects::nonNull)
                        .map(m -> PostUtils.toMediaResponse(m.getType(), m.getUrl(), m.getWidth(), m.getHeight()))
                        .toList())
                .build();
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }
}
