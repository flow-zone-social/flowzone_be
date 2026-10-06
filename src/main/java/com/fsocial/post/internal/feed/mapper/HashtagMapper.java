package com.fsocial.post.internal.feed.mapper;

import com.fsocial.post.internal.feed.dto.HashtagDTO;
import com.fsocial.post.internal.feed.dto.HashtagRequest;
import com.fsocial.post.internal.feed.entity.Hashtag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

    HashtagDTO toDTO(Hashtag hashtag);

    List<HashtagDTO> toListDTO(List<Hashtag> hashtags);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Hashtag toEntity(HashtagRequest request);
}
