package com.fsocial.transaction.internal.service;

import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.transaction.internal.dto.FinCategoryDTO;
import com.fsocial.transaction.internal.dto.FinCategoryRequest;
import com.fsocial.transaction.internal.entity.FinCategory;
import com.fsocial.transaction.internal.mapper.FinMapper;
import com.fsocial.transaction.internal.repository.FinCategoryRepository;
import com.fsocial.user.internal.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinCategoryServiceImpl implements FinCategoryService {

    FinCategoryRepository finCategoryRepository;
    UserRepository userRepository;
    FinMapper finMapper;

    @Override
    @Transactional
    public FinCategoryDTO create(String userId, FinCategoryRequest request) {
        if (finCategoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new AppException(StatusCode.FIN_CATEGORY_NAME_EXISTED);
        }
        FinCategory category = new FinCategory();
        finMapper.update(category, request);
        category.setUser(userRepository.getReferenceById(userId));
        return finMapper.toDTO(finCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public FinCategoryDTO update(String userId, String id, FinCategoryRequest request) {
        FinCategory category = findOwnedOrThrow(userId, id);
        if (finCategoryRepository.existsByUserIdAndNameAndIdNot(userId, request.getName(), id)) {
            throw new AppException(StatusCode.FIN_CATEGORY_NAME_EXISTED);
        }
        finMapper.update(category, request);
        category.setUpdatedAt(LocalDateTime.now());
        return finMapper.toDTO(finCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(String userId, String id) {
        // Product thuộc category này được DB tự bỏ liên kết (ON DELETE SET NULL).
        finCategoryRepository.delete(findOwnedOrThrow(userId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public FinCategoryDTO getById(String userId, String id) {
        return finMapper.toDTO(findOwnedOrThrow(userId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinCategoryDTO> getAll(String userId) {
        return finMapper.toCategoryDTOs(finCategoryRepository.findByUserId(userId));
    }

    /** Category của user khác coi như không tồn tại. */
    private FinCategory findOwnedOrThrow(String userId, String id) {
        return finCategoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(StatusCode.FIN_CATEGORY_NOT_FOUND));
    }
}
