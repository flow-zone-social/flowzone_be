package com.fsocial.media.internal.service;

import com.fsocial.media.api.MediaItemDTO;
import com.fsocial.media.api.AttachmentType;
import org.springframework.web.multipart.MultipartFile;

public interface UploadMedia {
    MediaItemDTO[] uploadMedia(MultipartFile[] files);
    MediaItemDTO[] uploadMedia(MultipartFile[] files, AttachmentType type);
    MediaItemDTO uploadSingleMedia(MultipartFile file);
    MediaItemDTO uploadSingleMedia(MultipartFile file, AttachmentType type);
}
