package com.fsocial.faq.internal.service;

import com.fsocial.faq.internal.dto.FaqDTO;
import com.fsocial.faq.internal.dto.FaqRatingRequest;
import com.fsocial.faq.internal.dto.FaqRequest;
import com.fsocial.faq.internal.enums.FaqType;

import java.util.List;

public interface FaqService {
    FaqDTO createFaq(FaqRequest request);
    FaqDTO updateFaq(String faqId, FaqRequest request);
    void deleteFaq(String faqId);
    FaqDTO getFaqById(String faqId, String userId);
    List<FaqDTO> getAllFaqs();
    List<FaqDTO> getFaqsByType(FaqType type);
    FaqDTO rateFaq(String faqId, String userId, FaqRatingRequest request);
}
