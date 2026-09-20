package com.fsocial.media.internal.repository;

import com.fsocial.media.internal.dto.AttachementProfileDTO;
import com.fsocial.media.internal.dto.AttachmentDTO;
import com.fsocial.media.internal.entity.Attachments;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentsRepository extends JpaRepository<Attachments, String> {

    List<AttachmentDTO> findByFileType(String fileType, Pageable pageable);

    @Modifying
    @Query("delete from Attachments a where a.ownerId = :postId")
    int deleteByOwnerId(@Param("postId") String postId);

    Window<AttachementProfileDTO> findByOwnerIdAndResourceType(String userId, String resourceType, Sort sort, ScrollPosition position);
}
