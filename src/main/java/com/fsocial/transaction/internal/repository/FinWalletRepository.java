package com.fsocial.transaction.internal.repository;

import com.fsocial.transaction.internal.entity.FinWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinWalletRepository extends JpaRepository<FinWallet, String> {

    Optional<FinWallet> findByUserId(String userId);

    /** Khóa dòng ví (SELECT ... FOR UPDATE) để các giao dịch đồng thời của cùng 1 user xếp hàng cập nhật số dư. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from FinWallet w where w.user.id = :userId")
    Optional<FinWallet> findByUserIdForUpdate(@Param("userId") String userId);
}
