package com.fsocial.media.internal.service;

import com.fsocial.shared.response.ApiDataCursor;
import com.fsocial.media.internal.dto.AttachementProfileDTO;
import com.fsocial.media.internal.dto.AttachmentDTO;
import com.fsocial.media.internal.dto.AttachmentsRequest;
import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.post.internal.entity.Post;
import com.fsocial.media.api.AttachmentType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.media.internal.mapper.AttachmentMapper;
import com.fsocial.user.internal.repository.AccountRepository;
import com.fsocial.media.internal.repository.AttachmentsRepository;
import com.fsocial.media.internal.service.AttachmentsService;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AttachmentsServiceImpl implements AttachmentsService {

    AttachmentsRepository attachmentsRepository;
    AttachmentMapper attachmentMapper;
    AccountRepository accountRepository;
    EntityManager entityManager;

    @Override
    public AttachmentDTO save(AttachmentDTO dto) {
        return attachmentMapper.toDTO(attachmentsRepository.save(Attachments.builder()
                .url(dto.getUrl())
                .resourceType(dto.getResourceType())
                .fileType(dto.getFileType())
                .size(dto.getSize())
                .ownerId(dto.getOwnerId())
                .publicId(dto.getPublicId())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .type(dto.getType())
                .build()));
    }

    @Override
    public Attachments linkToPost(String attachmentId, String postId, int ord) {
        Attachments attachment = attachmentsRepository.findById(attachmentId)
                .orElseThrow(() -> new AppException(StatusCode.NOT_FOUND));
        attachment.setPost(entityManager.getReference(Post.class, postId));
        attachment.setOrd(ord);
        attachment.setType(AttachmentType.POST);
        return attachmentsRepository.save(attachment);
    }

    @Override
    public ApiDataCursor<AttachementProfileDTO> getAttachmentByOwnerId(AttachmentsRequest dto, String userId) {


        Sort sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
        ScrollPosition position;

        if (dto.getLastItemId().isEmpty() && dto.getCreatedAt().isEmpty()) {
            // Trang đầu tiên - chưa có cursor
            position = ScrollPosition.keyset();
        } else {
            // Trang tiếp theo - có cursor
            position = ScrollPosition.forward(Map.of("createdAt", dto.getCreatedAt(), "id", dto.getLastItemId()));
        }

        Window<AttachementProfileDTO> listItem = attachmentsRepository
                .findByOwnerIdAndResourceType(userId, dto.getResourceType().toLowerCase(), sort, position);
        return ApiDataCursor.<AttachementProfileDTO>builder()
                .items(listItem.getContent())
                .hasMore(listItem.hasNext())
                .build();
    }

//    @Override
//    public List<AttachmentDTO> getAttachmentsByOwnerId(String ownerId, Pageable pageable) throws AppCheckedException {
//        userExists(ownerId);
//        return attachmentsRepository.getAttachmentsByOwnerId(ownerId, pageable);
//    }
//
//    @Override
//    public List<AttachmentDTO> getAttachmentsByType(String ownerId, String fileType, Pageable pageable) throws AppCheckedException {
//        userExists(ownerId);
//        return attachmentsRepository.getAttachmentsByOwnerId(fileType, pageable);
//    }

//    public void userExists(String userId) throws AppCheckedException {
//        Map<String, Boolean> userExists = accountRepository.e(userId).getData();
//        //check user exists
//        if (!userExists.get("exists")) {
//            throw new AppCheckedException("User not found", StatusCode.USER_NOT_FOUND);
//        }
//    }
}
