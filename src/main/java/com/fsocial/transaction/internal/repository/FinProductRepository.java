package com.fsocial.transaction.internal.repository;

import com.fsocial.transaction.internal.entity.FinProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinProductRepository extends JpaRepository<FinProduct, String> {
    List<FinProduct> findByUserId(String userId);

    List<FinProduct> findByUserIdAndFinCategoryId(String userId, String finCategoryId);

    Optional<FinProduct> findByIdAndUserId(String id, String userId);

    /** Tìm product trùng tên trong cùng category của user (dùng để chặn trùng khi tạo/sửa). */
    Optional<FinProduct> findByUserIdAndNameAndFinCategoryId(String userId, String name, String finCategoryId);

    /** Như trên nhưng cho product không thuộc category nào. */
    Optional<FinProduct> findByUserIdAndNameAndFinCategoryIsNull(String userId, String name);
}
