package com.fsocial.media.internal.mapper;

import com.fsocial.media.internal.dto.AttachmentDTO;
import com.fsocial.media.internal.entity.Attachments;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentDTO toDTO(Attachments attachments);
    Attachments toEntity(AttachmentDTO dto);
    List<AttachmentDTO> toDTOs(List<Attachments> attachments);
}
