package com.fsocial.media.internal.service;

import com.fsocial.shared.response.ApiDataCursor;
import com.fsocial.media.internal.dto.AttachementProfileDTO;
import com.fsocial.media.internal.dto.AttachmentDTO;
import com.fsocial.media.internal.dto.AttachmentsRequest;
import com.fsocial.media.internal.entity.Attachments;
import org.springframework.data.domain.Limit;

import java.util.List;

public interface AttachmentsService {

    AttachmentDTO save(AttachmentDTO dto);

    Attachments linkToPost(String attachmentId, String postId, int ord);

    Attachments findById(String attachmentId);

    //    List<AttachmentDTO> getAttachmentsByOwnerId(String ownerId, Pageable pageable) throws AppCheckedException;
//    List<AttachmentDTO> getAttachmentsByType(String ownerId,String fileType,  Pageable pageable) throws AppCheckedException;
    ApiDataCursor<AttachementProfileDTO> getAttachmentByOwnerId(AttachmentsRequest dto, String userId);
}
