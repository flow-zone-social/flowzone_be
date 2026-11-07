package com.fsocial.comment.internal.service;

import com.fsocial.comment.internal.dto.CommentDTORequest;
import com.fsocial.comment.internal.dto.CommentResponse;
import com.fsocial.comment.internal.dto.CommentUpdateDTORequest;
import com.fsocial.comment.internal.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(CommentDTORequest comment);

    boolean toggleLikeComment(String commentId, String userId);

    Integer countLike(String commentId, String userId);

    CommentResponse updateComment(CommentUpdateDTORequest comment);

    String deleteComment(String commentID);

    // Methods from timelineService
    List<CommentResponse> getComments(String postId);

    CommentResponse convertToCommentResponse(Comment comment);
}
