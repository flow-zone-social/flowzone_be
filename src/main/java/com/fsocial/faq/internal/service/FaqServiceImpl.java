package com.fsocial.faq.internal.service;

import com.fsocial.faq.api.FaqApi;
import com.fsocial.faq.api.FaqContent;
import com.fsocial.faq.internal.dto.FaqDTO;
import com.fsocial.faq.internal.dto.FaqRatingRequest;
import com.fsocial.faq.internal.dto.FaqRatingStatsDTO;
import com.fsocial.faq.internal.dto.FaqRequest;
import com.fsocial.media.api.MediaApi;
import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.faq.internal.entity.Faq;
import com.fsocial.faq.internal.entity.FaqRating;
import com.fsocial.faq.internal.entity.FaqView;
import com.fsocial.faq.internal.enums.FaqType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.faq.internal.mapper.FaqMapper;
import com.fsocial.faq.internal.repository.FaqRatingRepository;
import com.fsocial.faq.internal.repository.FaqRepository;
import com.fsocial.faq.internal.repository.FaqViewRepository;
import com.fsocial.faq.internal.service.FaqService;
import com.fsocial.user.internal.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService, FaqApi {

    FaqRepository faqRepository;
    FaqViewRepository faqViewRepository;
    UserRepository userRepository;
    FaqRatingRepository faqRatingRepository;
    MediaApi mediaApi;
    FaqMapper faqMapper;

    @Override
    public FaqDTO createFaq(FaqRequest request) {
        Faq faq = faqMapper.toEntity(request);
        faq.setAttachment(resolveAttachment(request.getAttachmentId()));
        return enrich(faqRepository.save(faq));
    }

    @Override
    public FaqDTO updateFaq(String faqId, FaqRequest request) {
        Faq faq = findFaqOrThrow(faqId);
        faq.setName(request.getName());
        faq.setDescription(request.getDescription());
        faq.setContent(request.getContent());
        faq.setType(request.getType());
        faq.setAttachment(resolveAttachment(request.getAttachmentId()));
        return enrich(faqRepository.save(faq));
    }

    @Override
    @Transactional
    public void deleteFaq(String faqId) {
        findFaqOrThrow(faqId);
        faqRatingRepository.deleteByFaqId(faqId);
        faqViewRepository.deleteByFaqId(faqId);
        faqRepository.deleteById(faqId);
    }

    @Override
    public FaqDTO getFaqById(String faqId, String userId) {
        Faq faq = findFaqOrThrow(faqId);
        faqViewRepository.save(FaqView.builder()
                .faq(faq)
                .user(userId == null ? null : userRepository.getReferenceById(userId))
                .build());
        return enrich(faq);
    }

    @Override
    public List<FaqContent> findAllContents() {
        return faqRepository.findAll().stream()
                .map(faq -> FaqContent.builder()
                        .name(faq.getName())
                        .description(faq.getDescription())
                        .content(faq.getContent())
                        .type(faq.getType().name())
                        .build())
                .toList();
    }

    @Override
    public List<FaqDTO> getAllFaqs() {
        return faqMapper.toListDTO(faqRepository.findAll()).stream()
                .map(this::withStats)
                .toList();
    }

    @Override
    public List<FaqDTO> getFaqsByType(FaqType type) {
        return faqMapper.toListDTO(faqRepository.findByType(type)).stream()
                .map(this::withStats)
                .toList();
    }

    @Override
    public FaqDTO rateFaq(String faqId, String userId, FaqRatingRequest request) {
        findFaqOrThrow(faqId);
        FaqRating rating = faqRatingRepository.findByFaqIdAndUserId(faqId, userId)
                .orElseGet(() -> FaqRating.builder().faqId(faqId).userId(userId).build());
        rating.setScore(request.getScore());
        rating.setUpdatedAt(LocalDateTime.now());
        faqRatingRepository.save(rating);
        return enrich(findFaqOrThrow(faqId));
    }

    private Faq findFaqOrThrow(String faqId) {
        return faqRepository.findById(faqId)
                .orElseThrow(() -> new AppException(StatusCode.FAQ_NOT_FOUND));
    }

    private Attachments resolveAttachment(String attachmentId) {
        if (attachmentId == null || attachmentId.isBlank()) {
            return null;
        }
        return mediaApi.findById(attachmentId);
    }

    private FaqDTO enrich(Faq faq) {
        return withStats(faqMapper.toDTO(faq));
    }

    private FaqDTO withStats(FaqDTO dto) {
        FaqRatingStatsDTO stats = faqRatingRepository.getStats(dto.getId());
        dto.setViewCount(faqViewRepository.countByFaqId(dto.getId()));
        dto.setRatingCount(stats.getRatingCount());
        dto.setAverageRating(stats.getAverageRating());
        return dto;
    }
}
