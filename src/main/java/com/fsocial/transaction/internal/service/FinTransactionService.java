package com.fsocial.transaction.internal.service;

import com.fsocial.shared.response.SearchPageResponse;
import com.fsocial.transaction.internal.dto.FinTransactionDTO;
import com.fsocial.transaction.internal.dto.FinTransactionRequest;
import com.fsocial.transaction.internal.entity.FinTransactionType;

public interface FinTransactionService {
    FinTransactionDTO create(String userId, FinTransactionRequest request);

    FinTransactionDTO update(String userId, String id, FinTransactionRequest request);

    void delete(String userId, String id);

    FinTransactionDTO getById(String userId, String id);

    /** @param type null = tất cả loại; page bắt đầu từ 0 */
    SearchPageResponse<FinTransactionDTO> getMyTransactions(String userId, FinTransactionType type, int page, int size);
}
