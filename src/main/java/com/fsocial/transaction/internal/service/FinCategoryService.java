package com.fsocial.transaction.internal.service;

import com.fsocial.transaction.internal.dto.FinCategoryDTO;
import com.fsocial.transaction.internal.dto.FinCategoryRequest;

import java.util.List;

/** Mọi thao tác chỉ tác động lên category của chính user (userId lấy từ JWT). */
public interface FinCategoryService {
    FinCategoryDTO create(String userId, FinCategoryRequest request);

    FinCategoryDTO update(String userId, String id, FinCategoryRequest request);

    void delete(String userId, String id);

    FinCategoryDTO getById(String userId, String id);

    List<FinCategoryDTO> getAll(String userId);
}
