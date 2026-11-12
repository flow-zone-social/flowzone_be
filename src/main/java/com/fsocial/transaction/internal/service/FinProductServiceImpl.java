package com.fsocial.transaction.internal.service;

import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.transaction.internal.dto.FinProductDTO;
import com.fsocial.transaction.internal.dto.FinProductRequest;
import com.fsocial.transaction.internal.entity.FinCategory;
import com.fsocial.transaction.internal.entity.FinProduct;
import com.fsocial.transaction.internal.mapper.FinMapper;
import com.fsocial.transaction.internal.repository.FinCategoryRepository;
import com.fsocial.transaction.internal.repository.FinProductRepository;
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
public class FinProductServiceImpl implements FinProductService {

    FinProductRepository finProductRepository;
    FinCategoryRepository finCategoryRepository;
    UserRepository userRepository;
    FinMapper finMapper;

    @Override
    @Transactional
    public FinProductDTO create(String userId, FinProductRequest request) {
        FinCategory category = resolveCategory(userId, request.getFinCategoryId());
        assertNameAvailable(userId, category, request.getName(), null);
        FinProduct product = new FinProduct();
        finMapper.update(product, request);
        product.setFinCategory(category);
        product.setUser(userRepository.getReferenceById(userId));
        return finMapper.toDTO(finProductRepository.save(product));
    }

    @Override
    @Transactional
    public FinProductDTO update(String userId, String id, FinProductRequest request) {
        FinProduct product = findOwnedOrThrow(userId, id);
        FinCategory category = resolveCategory(userId, request.getFinCategoryId());
        assertNameAvailable(userId, category, request.getName(), id);
        finMapper.update(product, request);
        product.setFinCategory(category);
        product.setUpdatedAt(LocalDateTime.now());
        return finMapper.toDTO(finProductRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(String userId, String id) {
        // Giao dịch đang dùng product này được DB tự bỏ liên kết (ON DELETE SET NULL).
        finProductRepository.delete(findOwnedOrThrow(userId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public FinProductDTO getById(String userId, String id) {
        return finMapper.toDTO(findOwnedOrThrow(userId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinProductDTO> getAll(String userId, String finCategoryId) {
        List<FinProduct> products = (finCategoryId == null || finCategoryId.isBlank())
                ? finProductRepository.findByUserId(userId)
                : finProductRepository.findByUserIdAndFinCategoryId(userId, finCategoryId);
        return finMapper.toProductDTOs(products);
    }

    private FinProduct findOwnedOrThrow(String userId, String id) {
        return finProductRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(StatusCode.FIN_PRODUCT_NOT_FOUND));
    }

    /** null/rỗng = không thuộc category nào; còn lại phải là category của chính user. */
    private FinCategory resolveCategory(String userId, String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return null;
        }
        return finCategoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new AppException(StatusCode.FIN_CATEGORY_NOT_FOUND));
    }

    /** Tên product duy nhất trong cùng (user, category); selfId = product đang sửa, bỏ qua chính nó. */
    private void assertNameAvailable(String userId, FinCategory category, String name, String selfId) {
        boolean taken = (category == null
                ? finProductRepository.findByUserIdAndNameAndFinCategoryIsNull(userId, name)
                : finProductRepository.findByUserIdAndNameAndFinCategoryId(userId, name, category.getId()))
                .filter(p -> !p.getId().equals(selfId))
                .isPresent();
        if (taken) {
            throw new AppException(StatusCode.FIN_PRODUCT_NAME_EXISTED);
        }
    }
}
