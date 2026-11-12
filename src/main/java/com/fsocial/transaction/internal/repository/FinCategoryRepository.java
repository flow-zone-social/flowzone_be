package com.fsocial.transaction.internal.repository;

import com.fsocial.transaction.internal.entity.FinCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinCategoryRepository extends JpaRepository<FinCategory, String> {
    List<FinCategory> findByUserId(String userId);

    Optional<FinCategory> findByIdAndUserId(String id, String userId);

    boolean existsByUserIdAndName(String userId, String name);

    boolean existsByUserIdAndNameAndIdNot(String userId, String name, String id);
}
