package com.fsocial.transaction.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.shared.response.SearchPageResponse;
import com.fsocial.transaction.internal.dto.FinTransactionDTO;
import com.fsocial.transaction.internal.dto.FinTransactionRequest;
import com.fsocial.transaction.internal.entity.FinTransactionType;
import com.fsocial.transaction.internal.service.FinTransactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/fin/transactions")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FinTransactionController {

    FinTransactionService finTransactionService;

    @PostMapping
    public ApiResponse<FinTransactionDTO> create(@RequestBody @Valid FinTransactionRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinTransactionDTO>builder()
                .message("Tạo giao dịch thành công")
                .data(finTransactionService.create(jwt.getSubject(), request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<FinTransactionDTO> update(@PathVariable String id,
                                                 @RequestBody @Valid FinTransactionRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinTransactionDTO>builder()
                .message("Cập nhật giao dịch thành công")
                .data(finTransactionService.update(jwt.getSubject(), id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        finTransactionService.delete(jwt.getSubject(), id);
        return ApiResponse.<Void>builder().message("Xóa giao dịch thành công").build();
    }

    @GetMapping("/{id}")
    public ApiResponse<FinTransactionDTO> getById(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinTransactionDTO>builder()
                .message("Lấy chi tiết giao dịch thành công")
                .data(finTransactionService.getById(jwt.getSubject(), id))
                .build();
    }

    @GetMapping
    public ApiResponse<SearchPageResponse<FinTransactionDTO>> getMyTransactions(
            @RequestParam(required = false) FinTransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<SearchPageResponse<FinTransactionDTO>>builder()
                .message("Lấy danh sách giao dịch thành công")
                .data(finTransactionService.getMyTransactions(jwt.getSubject(), type, page, size))
                .build();
    }
}
