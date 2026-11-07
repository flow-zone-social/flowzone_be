package com.fsocial.media.internal.mapper;

import com.fsocial.media.internal.dto.AttachmentDTO;
import com.fsocial.media.internal.entity.Attachments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    AttachmentDTO toDTO(Attachments attachments);
    @Mapping(target = "owner", ignore = true)
    Attachments toEntity(AttachmentDTO dto);
    List<AttachmentDTO> toDTOs(List<Attachments> attachments);
}
