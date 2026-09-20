package com.fsocial.user.internal.service;

import com.fsocial.user.internal.dto.ProfileResponse;
import com.fsocial.user.internal.dto.UpdateProfileRequest;
import com.fsocial.user.internal.dto.AccountResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponse updateAvatar(MultipartFile file, String userId);
    ProfileResponse updateBackground(MultipartFile file, String userId);
    AccountResponse updatePersonalInfo(UpdateProfileRequest request, String userId);
    String uploadImage(MultipartFile file);
}
