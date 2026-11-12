package com.fsocial.transaction.internal.mapper;

import com.fsocial.transaction.internal.dto.*;
import com.fsocial.transaction.internal.entity.FinCategory;
import com.fsocial.transaction.internal.entity.FinProduct;
import com.fsocial.transaction.internal.entity.FinTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FinMapper {

    // ----- Category -----
    FinCategoryDTO toDTO(FinCategory category);

    List<FinCategoryDTO> toCategoryDTOs(List<FinCategory> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "finProducts", ignore = true)
    @Mapping(target = "user", ignore = true)
    void update(@MappingTarget FinCategory category, FinCategoryRequest request);

    // ----- Product -----
    @Mapping(target = "finCategoryId", source = "finCategory.id")
    @Mapping(target = "finCategoryName", source = "finCategory.name")
    FinProductDTO toDTO(FinProduct product);

    List<FinProductDTO> toProductDTOs(List<FinProduct> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "finCategory", ignore = true)
    @Mapping(target = "user", ignore = true)
    void update(@MappingTarget FinProduct product, FinProductRequest request);

    // ----- Transaction -----
    @Mapping(target = "finProductId", source = "finProduct.id")
    @Mapping(target = "finProductName", source = "finProduct.name")
    @Mapping(target = "finCategoryId", source = "finProduct.finCategory.id")
    @Mapping(target = "finCategoryName", source = "finProduct.finCategory.name")
    FinTransactionDTO toDTO(FinTransaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isBlocked", ignore = true)
    @Mapping(target = "finProduct", ignore = true)
    @Mapping(target = "user", ignore = true)
    void update(@MappingTarget FinTransaction transaction, FinTransactionRequest request);
}
