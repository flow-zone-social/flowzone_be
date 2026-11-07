package com.fsocial.post.internal.util;

import com.fsocial.post.internal.dto.ContentResponse;
import com.fsocial.post.internal.dto.MediaResponse;
import com.fsocial.post.internal.dto.PostFeedRow;
import com.fsocial.post.internal.dto.PostResponse;
import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.post.internal.entity.Post;
import com.fsocial.media.api.MediaLayoutType;
import com.fsocial.media.api.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostUtils {

    /** likeCount / isLike vẫn ở bảng riêng (post_like) nên phải truyền vào; tags đọc thẳng từ entity. */
    public static PostResponse buildPostResponse(Post post, ActorSnapshotDTO owner, int commentCount,
                                                 int likeCount, boolean liked) {
        return PostResponse.builder()
                .id(post.getId())
                .originPostId(post.getOriginPostId())
                .content(buildContentResponse(post))
                .countLikes(likeCount)
                .countComments(commentCount)
                .userId(post.getOwner().getId())
                .displayName(owner == null ? "" : owner.getDisplayName())
                .avatar(owner == null ? null : owner.getAvatar())
                .createDatetime(post.getCreateDatetime())
                .isLike(liked)
                .isShare(Boolean.TRUE.equals(post.getIsShare()))
                .status(Boolean.TRUE.equals(post.getStatus()))
                .tags(post.getTags() == null ? List.of() : post.getTags())
                .build();
    }

    /** Dùng cho getPostsByUser: media được truyền vào riêng (đã fetch gộp theo postIds), không đọc post.getMedia(). */
    public static PostResponse buildPostResponseFromFeedRow(PostFeedRow row, List<Attachments> media) {
        Post post = row.post();
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getOwner().getId())
                .originPostId(post.getOriginPostId())
                .content(ContentResponse.builder()
                        .text(post.getText())
                        .html(post.getHtml())
                        .media(buildMediaResponse(media))
                        .build())
                .countLikes(row.likeCount() == null ? 0 : row.likeCount().intValue())
                .countComments(row.commentCount() == null ? 0 : row.commentCount().intValue())
                .displayName(row.displayName() == null ? "" : row.displayName())
                .avatar(row.avatar())
                .createDatetime(post.getCreateDatetime())
                .isLike(row.isLikedByRequester())
                .isShare(Boolean.TRUE.equals(post.getIsShare()))
                .status(Boolean.TRUE.equals(post.getStatus()))
                .tags(post.getTags() == null ? List.of() : post.getTags())
                .build();
    }

    public static ContentResponse buildContentResponse(Post post) {
        return ContentResponse.builder()
                .html(post.getHtml())
                .text(post.getText())
                .media(buildMediaResponse(post.getMedia()))
                .build();
    }

    public static List<MediaResponse> buildMediaResponse(List<Attachments> mediaItems) {
        if (mediaItems == null || mediaItems.isEmpty()) {
            return new ArrayList<>();
        }
        return mediaItems.stream()
                .filter(Objects::nonNull)
                .map(item -> toMediaResponse(MediaType.fromValue(item.getResourceType()), item.getUrl(), item.getWidth(), item.getHeight()))
                .toList();
    }

    /** Dùng chung cho attachments và comment_media — hai entity khác nhau nhưng cùng bộ field. */
    public static MediaResponse toMediaResponse(MediaType type, String url, Integer width, Integer height) {
        double ratio = (width == null || height == null || height == 0)
                ? 1.0
                : (double) width / height;
        return MediaResponse.builder()
                .type(type == null ? null : type.value())
                .url(url)
                .width(width)
                .height(height)
                .ratio(ratio)
                .mediaType(calculateLayoutType(ratio))
                .build();
    }

    public static MediaLayoutType calculateLayoutType(double ratio) {
        if (ratio > 2.0) {
            return MediaLayoutType.PANORAMA;
        } else if (ratio > 1.2) {
            return MediaLayoutType.LANDSCAPE;
        } else if (ratio > 0.9) {
            return MediaLayoutType.SQUARE;
        } else {
            return MediaLayoutType.PORTRAIT;
        }
    }
}
