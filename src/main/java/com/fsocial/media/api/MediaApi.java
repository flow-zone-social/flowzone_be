package com.fsocial.media.api;

import com.fsocial.media.internal.entity.Attachments;
import org.springframework.web.multipart.MultipartFile;

public interface MediaApi {

    MediaItemDTO[] uploadValidMedia(MultipartFile[] media);

    MediaItemDTO[] uploadValidMedia(MultipartFile[] media, AttachmentType type);

    MediaItemDTO uploadSingleMedia(MultipartFile file, AttachmentType type);

    // TODO(modulith): tra ve entity Attachments qua api/ — ke thua ngoai le quan he JPA
    // Post.media/Attachments.post (chuyen sang ID-based o Buoc 3).
    Attachments linkToPost(String attachmentId, String postId, int ord);

    // TODO(modulith): tra ve entity Attachments qua api/ — ke thua ngoai le quan he JPA
    // Faq.attachment (chuyen sang ID-based o Buoc 3).
    Attachments findById(String attachmentId);
}
