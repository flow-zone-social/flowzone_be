package com.fsocial.media.internal.service;

import com.fsocial.media.api.AttachmentType;
import com.fsocial.media.api.MediaApi;
import com.fsocial.media.api.MediaItemDTO;
import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.media.internal.util.MediaUploadUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MediaApiImpl implements MediaApi {

    AttachmentsService attachmentsService;
    MediaUploadUtils mediaUploadUtils;
    UploadMedia uploadMedia;

    @Override
    public MediaItemDTO[] uploadValidMedia(MultipartFile[] media) {
        return mediaUploadUtils.uploadValidMedia(media);
    }

    @Override
    public MediaItemDTO[] uploadValidMedia(MultipartFile[] media, AttachmentType type) {
        return mediaUploadUtils.uploadValidMedia(media, type);
    }

    @Override
    public MediaItemDTO uploadSingleMedia(MultipartFile file, AttachmentType type) {
        return uploadMedia.uploadSingleMedia(file, type);
    }

    @Override
    public Attachments linkToPost(String attachmentId, String postId, int ord) {
        return attachmentsService.linkToPost(attachmentId, postId, ord);
    }

    @Override
    public Attachments findById(String attachmentId) {
        return attachmentsService.findById(attachmentId);
    }
}
