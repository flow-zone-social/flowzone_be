package com.fsocial.post.internal.dto;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.post.api.ContentDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDTO {
    String id;
    String userId;
    LocalDateTime createDatetime;
    ContentDTO content;
    String originPostId;
    List<String> likes = new ArrayList<>();
    Boolean isShare = false;
    Boolean status = true;
    ActorSnapshotDTO owner;
    List<String> tags = new ArrayList<>();
}
