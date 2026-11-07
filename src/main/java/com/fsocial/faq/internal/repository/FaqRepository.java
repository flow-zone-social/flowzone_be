package com.fsocial.faq.internal.repository;

import com.fsocial.faq.internal.entity.Faq;
import com.fsocial.faq.internal.enums.FaqType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, String> {
    List<Faq> findByType(FaqType type);
}
