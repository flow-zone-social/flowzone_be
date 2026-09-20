package com.fsocial.user.internal.controller;

import com.fsocial.shared.response.ApiDataCursor;
import com.fsocial.shared.response.ApiResponse;
import com.fsocial.post.internal.dto.PostResponse;
import com.fsocial.user.internal.dto.ProfileResponse;
import com.fsocial.user.internal.dto.UpdateProfileRequest;
import com.fsocial.user.internal.dto.AccountResponse;
import com.fsocial.user.internal.service.AccountService;
import com.fsocial.post.internal.service.PostService;
import com.fsocial.user.internal.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/profile")
@Slf4j
public class ProfileController {

    AccountService accountService;
    ProfileService profileService;
    PostService postService;

    @GetMapping()
    public ApiResponse<AccountResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {

        return ApiResponse.<AccountResponse>builder()
                .data(accountService.getUser(jwt.getSubject()))
                .message("Lấy thông tin tài khoản thành công")
                .build();
    }

    @GetMapping("/post")
    public ApiResponse<ApiDataCursor<PostResponse>> getPostProfile(@AuthenticationPrincipal Jwt jwt, @RequestParam(name = "last-item") String lastItem,
                                                                   @RequestParam(name = "created-at") String createAt) {
        int size = 10;
        return ApiResponse.<ApiDataCursor<PostResponse>>builder()
                .data(postService.getMyPost(jwt.getSubject(), lastItem, createAt))
                .message("Lấy thông tin tài khoản thành công")
                .build();
    }

    @PutMapping("/update")
    public ApiResponse<AccountResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.<AccountResponse>builder()
                .data(profileService.updatePersonalInfo(request, jwt.getSubject()))
                .message("Cập nhật thông tin cá nhân thành công")
                .build();
    }

    @PutMapping("/update-avatar")
    public ApiResponse<ProfileResponse> updateAvatar(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        return ApiResponse.<ProfileResponse>builder()
                .data(profileService.updateAvatar(file, userId))
                .message("Cập nhật ảnh đại diện thành công")
                .build();
    }

    @PutMapping("/update-background")
    public ApiResponse<ProfileResponse> updateBackground(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        return ApiResponse.<ProfileResponse>builder()
                .data(profileService.updateBackground(file, userId))
                .message("Cập nhật ảnh nền thành công")
                .build();
    }

//    @GetMapping("/images")
//    public ApiResponse<>
}
