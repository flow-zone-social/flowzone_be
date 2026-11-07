package com.fsocial.faq.internal.mapper;

import com.fsocial.faq.internal.dto.FaqDTO;
import com.fsocial.faq.internal.dto.FaqRequest;
import com.fsocial.faq.internal.entity.Faq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FaqMapper {

    @Mapping(target = "attachmentId", source = "attachment.id")
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    FaqDTO toDTO(Faq faq);

    List<FaqDTO> toListDTO(List<Faq> faqs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachment", ignore = true)
    Faq toEntity(FaqRequest request);
}
