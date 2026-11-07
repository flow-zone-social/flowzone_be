package com.fsocial.faq.internal.repository;

import com.fsocial.faq.internal.entity.FaqView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqViewRepository extends JpaRepository<FaqView, String> {

    long countByFaqId(String faqId);

    @Query("select count(distinct v.user.id) from FaqView v where v.faq.id = :faqId")
    long countDistinctUserIdByFaqId(@Param("faqId") String faqId);

    @Modifying
    @Query("delete from FaqView v where v.faq.id = :faqId")
    int deleteByFaqId(@Param("faqId") String faqId);
}
