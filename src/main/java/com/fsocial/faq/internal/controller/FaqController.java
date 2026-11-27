package com.fsocial.faq.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.faq.internal.dto.FaqDTO;
import com.fsocial.faq.internal.dto.FaqRatingRequest;
import com.fsocial.faq.internal.dto.FaqRequest;
import com.fsocial.faq.internal.enums.FaqType;
import com.fsocial.faq.internal.service.FaqService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/faq")
public class FaqController {

    FaqService faqService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<FaqDTO> createFaq(@RequestBody @Valid FaqRequest request) {
        return ApiResponse.<FaqDTO>builder()
                .message("Guide created successfully")
                .data(faqService.createFaq(request))
                .build();
    }

    @PutMapping("/{faqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<FaqDTO> updateFaq(@PathVariable String faqId, @RequestBody @Valid FaqRequest request) {
        return ApiResponse.<FaqDTO>builder()
                .message("Guide updated successfully")
                .data(faqService.updateFaq(faqId, request))
                .build();
    }

    @DeleteMapping("/{faqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteFaq(@PathVariable String faqId) {
        faqService.deleteFaq(faqId);
        return ApiResponse.<Void>builder()
                .message("Guide deleted successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<List<FaqDTO>> getAllFaqs() {
        return ApiResponse.<List<FaqDTO>>builder()
                .message("Guides retrieved successfully")
                .data(faqService.getAllFaqs())
                .build();
    }

    @GetMapping("/type/{type}")
    public ApiResponse<List<FaqDTO>> getFaqsByType(@PathVariable FaqType type) {
        return ApiResponse.<List<FaqDTO>>builder()
                .message("Guides retrieved by type successfully")
                .data(faqService.getFaqsByType(type))
                .build();
    }

    @GetMapping("/{faqId}")
    public ApiResponse<FaqDTO> getFaqById(@PathVariable String faqId, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FaqDTO>builder()
                .message("Guide details retrieved successfully")
                .data(faqService.getFaqById(faqId, jwt.getSubject()))
                .build();
    }

    @PostMapping("/{faqId}/rating")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<FaqDTO> rateFaq(@PathVariable String faqId, @RequestBody @Valid FaqRatingRequest request,
                                        @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FaqDTO>builder()
                .message("Guide rated successfully")
                .data(faqService.rateFaq(faqId, jwt.getSubject(), request))
                .build();
    }
}
