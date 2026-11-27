package com.fsocial.transaction.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.transaction.internal.dto.FinCategoryDTO;
import com.fsocial.transaction.internal.dto.FinCategoryRequest;
import com.fsocial.transaction.internal.service.FinCategoryService;
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
@RequestMapping("/fin/categories")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FinCategoryController {

    FinCategoryService finCategoryService;

    @PostMapping
    public ApiResponse<FinCategoryDTO> create(@RequestBody @Valid FinCategoryRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinCategoryDTO>builder()
                .message("Category created successfully")
                .data(finCategoryService.create(jwt.getSubject(), request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<FinCategoryDTO> update(@PathVariable String id, @RequestBody @Valid FinCategoryRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinCategoryDTO>builder()
                .message("Category updated successfully")
                .data(finCategoryService.update(jwt.getSubject(), id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        finCategoryService.delete(jwt.getSubject(), id);
        return ApiResponse.<Void>builder().message("Category deleted successfully").build();
    }

    @GetMapping
    public ApiResponse<List<FinCategoryDTO>> getAll(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<List<FinCategoryDTO>>builder()
                .message("Categories retrieved successfully")
                .data(finCategoryService.getAll(jwt.getSubject()))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<FinCategoryDTO> getById(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinCategoryDTO>builder()
                .message("Category details retrieved successfully")
                .data(finCategoryService.getById(jwt.getSubject(), id))
                .build();
    }
}
