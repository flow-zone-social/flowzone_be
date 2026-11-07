package com.fsocial.comment.internal.dto;

import com.fsocial.post.internal.dto.ContentResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ReplyCommentResponse {
    String id;
    String commentId;
    String userId;
    ContentResponse content;
    Integer countLikes;
    String displayName;
    String avatar;
    LocalDateTime createDatetime;
    boolean like;
}
