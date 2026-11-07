package com.fsocial.user.internal.service;

import com.fsocial.user.internal.dto.ProfileResponse;
import com.fsocial.user.internal.dto.UpdateProfileRequest;
import com.fsocial.user.internal.dto.AccountResponse;
import com.fsocial.user.internal.entity.Account;
import com.fsocial.media.api.AttachmentType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.user.internal.mapper.AccountMapper;
import com.fsocial.user.internal.repository.AccountRepository;
import com.fsocial.user.internal.repository.FollowRepository;
import com.fsocial.user.internal.service.ProfileService;
import com.fsocial.media.api.MediaApi;
import com.fsocial.user.internal.util.DisplayNameUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    AccountRepository accountRepository;
    FollowRepository followRepository;
    AccountMapper accountMapper;
    MediaApi mediaApi;

    @Override
    public ProfileResponse updateAvatar(MultipartFile file, String userId) {
        String avatar = uploadImage(file, AttachmentType.AVATAR);
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(StatusCode.UPLOAD_AVATAR_FAIL));

        account.setAvatar(avatar);
        return accountMapper.toProfileResponse(accountRepository.save(account));
    }

    @Override
    public ProfileResponse updateBackground(MultipartFile file, String userId) {
        String background = uploadImage(file, AttachmentType.BACKGROUND);
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(StatusCode.UPLOAD_AVATAR_FAIL));

        account.setBackground(background);
        return accountMapper.toProfileResponse(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountResponse updatePersonalInfo(UpdateProfileRequest request, String userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(StatusCode.USER_NOT_FOUND));

        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setDob(request.getDob());
        account.setGender(request.getGender());
        account.setAddress(request.getAddress());
        account.setBio(request.getBio());

        Account updatedAccount = accountRepository.save(account);

        return AccountResponse.builder()
                .id(updatedAccount.getId())
                .firstName(updatedAccount.getFirstName())
                .lastName(updatedAccount.getLastName())
                .dob(updatedAccount.getDob() != null ? updatedAccount.getDob().toString() : null)
                .gender(updatedAccount.getGender())
                .address(updatedAccount.getAddress())
                .displayName(DisplayNameUtils.build(updatedAccount))
                .background(updatedAccount.getBackground())
                .bio(updatedAccount.getBio())
                .build();
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return uploadImage(file, null);
    }

    private String uploadImage(MultipartFile file, AttachmentType type) {
        return mediaApi.uploadSingleMedia(file, type).getUrl();
    }
}
