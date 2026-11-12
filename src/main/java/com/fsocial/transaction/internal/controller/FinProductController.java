package com.fsocial.transaction.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.transaction.internal.dto.FinProductDTO;
import com.fsocial.transaction.internal.dto.FinProductRequest;
import com.fsocial.transaction.internal.service.FinProductService;
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
@RequestMapping("/fin/products")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FinProductController {

    FinProductService finProductService;

    @PostMapping
    public ApiResponse<FinProductDTO> create(@RequestBody @Valid FinProductRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinProductDTO>builder()
                .message("Tạo phân loại thành công")
                .data(finProductService.create(jwt.getSubject(), request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<FinProductDTO> update(@PathVariable String id, @RequestBody @Valid FinProductRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinProductDTO>builder()
                .message("Cập nhật phân loại thành công")
                .data(finProductService.update(jwt.getSubject(), id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        finProductService.delete(jwt.getSubject(), id);
        return ApiResponse.<Void>builder().message("Xóa phân loại thành công").build();
    }

    @GetMapping
    public ApiResponse<List<FinProductDTO>> getAll(@RequestParam(required = false) String categoryId,
                                                    @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<List<FinProductDTO>>builder()
                .message("Lấy danh sách phân loại thành công")
                .data(finProductService.getAll(jwt.getSubject(), categoryId))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<FinProductDTO> getById(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinProductDTO>builder()
                .message("Lấy chi tiết phân loại thành công")
                .data(finProductService.getById(jwt.getSubject(), id))
                .build();
    }
}
