package com.fsocial.transaction.internal.service;

import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.shared.response.SearchPageResponse;
import com.fsocial.transaction.internal.dto.FinTransactionDTO;
import com.fsocial.transaction.internal.dto.FinTransactionRequest;
import com.fsocial.transaction.internal.entity.FinProduct;
import com.fsocial.transaction.internal.entity.FinTransaction;
import com.fsocial.transaction.internal.entity.FinTransactionType;
import com.fsocial.transaction.internal.mapper.FinMapper;
import com.fsocial.transaction.internal.repository.FinProductRepository;
import com.fsocial.transaction.internal.repository.FinTransactionRepository;
import com.fsocial.user.api.UserApi;
import com.fsocial.user.internal.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinTransactionServiceImpl implements FinTransactionService {

    static final int MAX_PAGE_SIZE = 100;

    FinTransactionRepository finTransactionRepository;
    FinProductRepository finProductRepository;
    UserApi userApi;
    FinMapper finMapper;

    @Override
    @Transactional
    public FinTransactionDTO create(String userId, FinTransactionRequest request) {
        FinTransaction transaction = new FinTransaction();
        finMapper.update(transaction, request);
        transaction.setFinProduct(resolveProduct(userId, request.getFinProductId()));
        transaction.setUser(userApi.findById(userId));
        return finMapper.toDTO(finTransactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public FinTransactionDTO update(String userId, String id, FinTransactionRequest request) {
        FinTransaction transaction = findOwnedOrThrow(userId, id);
        finMapper.update(transaction, request);
        transaction.setFinProduct(resolveProduct(userId, request.getFinProductId()));
        transaction.setUpdatedAt(LocalDateTime.now());
        return finMapper.toDTO(finTransactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void delete(String userId, String id) {
        finTransactionRepository.delete(findOwnedOrThrow(userId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public FinTransactionDTO getById(String userId, String id) {
        return finMapper.toDTO(findOwnedOrThrow(userId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public SearchPageResponse<FinTransactionDTO> getMyTransactions(String userId, FinTransactionType type,
                                                                   int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<FinTransaction> result = type == null
                ? finTransactionRepository.findByUserId(userId, pageable)
                : finTransactionRepository.findByUserIdAndType(userId, type, pageable);

        return new SearchPageResponse<>(
                result.getContent().stream().map(finMapper::toDTO).toList(),
                safePage, safeSize, result.hasNext());
    }

    /** Chỉ trả giao dịch của chính user — giao dịch của người khác coi như không tồn tại. */
    private FinTransaction findOwnedOrThrow(String userId, String id) {
        return finTransactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(StatusCode.FIN_TRANSACTION_NOT_FOUND));
    }

    /** null/rỗng = chưa phân loại; còn lại phải là product của chính user. */
    private FinProduct resolveProduct(String userId, String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return finProductRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(StatusCode.FIN_PRODUCT_NOT_FOUND));
    }
}
