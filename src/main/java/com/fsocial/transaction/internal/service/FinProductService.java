package com.fsocial.transaction.internal.service;

import com.fsocial.transaction.internal.dto.FinProductDTO;
import com.fsocial.transaction.internal.dto.FinProductRequest;

import java.util.List;

/** Mọi thao tác chỉ tác động lên product thuộc category của chính user. */
public interface FinProductService {
    FinProductDTO create(String userId, FinProductRequest request);

    FinProductDTO update(String userId, String id, FinProductRequest request);

    void delete(String userId, String id);

    FinProductDTO getById(String userId, String id);

    /** @param finCategoryId null = lấy tất cả product của user */
    List<FinProductDTO> getAll(String userId, String finCategoryId);
}
