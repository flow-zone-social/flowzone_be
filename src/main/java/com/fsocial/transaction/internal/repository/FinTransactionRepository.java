package com.fsocial.transaction.internal.repository;

import com.fsocial.transaction.internal.entity.FinTransaction;
import com.fsocial.transaction.internal.entity.FinTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinTransactionRepository extends JpaRepository<FinTransaction, String> {
    Page<FinTransaction> findByUserId(String userId, Pageable pageable);

    Page<FinTransaction> findByUserIdAndType(String userId, FinTransactionType type, Pageable pageable);

    Optional<FinTransaction> findByIdAndUserId(String id, String userId);
}
