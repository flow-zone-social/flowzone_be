package com.fsocial.comment.internal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LikeReplyCommentDTO {
    @NotBlank(message = "Reply comment ID is required")
    String replyCommentId;
    String userId;
}
