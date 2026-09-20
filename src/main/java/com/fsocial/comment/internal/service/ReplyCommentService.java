package com.fsocial.comment.internal.service;

import com.fsocial.comment.internal.dto.LikeReplyCommentDTO;
import com.fsocial.comment.internal.dto.ReplyCommentRequest;
import com.fsocial.comment.internal.dto.ReplyCommentResponse;
import com.fsocial.comment.internal.dto.ReplyCommentUpdateDTORequest;

import java.util.List;

public interface ReplyCommentService {

    ReplyCommentResponse addReplyComment(ReplyCommentRequest request);

    ReplyCommentResponse updateReplyComment(ReplyCommentUpdateDTORequest request);

    String deleteReplyComment(String replyCommentId);

    // Methods from timelineService
    List<ReplyCommentResponse> getReplyCommentsByCommentId(String commentId);

    // Method for like reply comment
    boolean likeReplyComment(LikeReplyCommentDTO request);
}
