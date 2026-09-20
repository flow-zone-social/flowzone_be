package com.fsocial.comment.internal.dto;

import com.fsocial.post.api.ContentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    String id;
    String postId;
    String userId;
    ContentDTO content;
    int countLikes;
    int countReplyComment;
    boolean reply;
}
