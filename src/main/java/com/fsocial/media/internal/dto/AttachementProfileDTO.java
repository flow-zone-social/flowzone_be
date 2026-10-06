package com.fsocial.media.internal.dto;

import com.fsocial.media.api.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public  class AttachementProfileDTO {
    String id;
    String publicId;
    String url;
    String resourceType;
    String fileType;
    String size;
    String ownerId;
    String postId;
    Integer ord;
    Integer width;
    Integer height;
    AttachmentType type;
    LocalDateTime createdAt;
}
