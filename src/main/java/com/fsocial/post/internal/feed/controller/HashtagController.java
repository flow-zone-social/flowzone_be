package com.fsocial.post.internal.feed.controller;

import com.fsocial.post.internal.feed.dto.HashtagDTO;
import com.fsocial.post.internal.feed.dto.HashtagRequest;
import com.fsocial.post.internal.feed.dto.HashtagSuggestion;
import com.fsocial.post.internal.feed.service.HashtagService;
import com.fsocial.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/hashtag")
public class HashtagController {

    HashtagService hashtagService;

    @GetMapping
    public ApiResponse<List<HashtagDTO>> getAllHashtags() {
        return ApiResponse.<List<HashtagDTO>>builder()
                .message("Lấy danh sách hashtag thành công")
                .data(hashtagService.getAllHashtags())
                .build();
    }

    @GetMapping("/suggestions")
    public ApiResponse<List<HashtagSuggestion>> getSuggestions(@RequestParam String name) {
        return ApiResponse.<List<HashtagSuggestion>>builder()
                .message("Lấy gợi ý hashtag thành công")
                .data(hashtagService.findHastagByName(name))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<HashtagDTO> getHashtagById(@PathVariable String id) {
        return ApiResponse.<HashtagDTO>builder()
                .message("Lấy chi tiết hashtag thành công")
                .data(hashtagService.getHashtagById(id))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<HashtagDTO> createHashtag(@RequestBody @Valid HashtagRequest request) {
        return ApiResponse.<HashtagDTO>builder()
                .message("Tạo hashtag thành công")
                .data(hashtagService.createHashtag(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<HashtagDTO> updateHashtag(@PathVariable String id, @RequestBody @Valid HashtagRequest request) {
        return ApiResponse.<HashtagDTO>builder()
                .message("Cập nhật hashtag thành công")
                .data(hashtagService.updateHashtag(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Void> deleteHashtag(@PathVariable String id) {
        hashtagService.deleteHashtag(id);
        return ApiResponse.<Void>builder()
                .message("Xóa hashtag thành công")
                .build();
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<HashtagDTO> restoreHashtag(@PathVariable String id) {
        return ApiResponse.<HashtagDTO>builder()
                .message("Khôi phục hashtag thành công")
                .data(hashtagService.restoreHashtag(id))
                .build();
    }
}
