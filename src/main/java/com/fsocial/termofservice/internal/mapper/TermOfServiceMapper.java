package com.fsocial.termofservice.internal.mapper;

import com.fsocial.termofservice.internal.dto.TermOfServiceDTO;
import com.fsocial.termofservice.internal.entity.TermOfServices;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TermOfServiceMapper {
    TermOfServiceDTO toDTO(TermOfServices termOfServices);
    TermOfServices toEntity(TermOfServiceDTO termOfServiceDTO);
    List<TermOfServiceDTO> toListDTO(List<TermOfServices> termOfServices);
}
