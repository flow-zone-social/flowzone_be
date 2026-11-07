package com.fsocial.user.internal.service;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.user.api.UserApi;
import com.fsocial.shared.response.ApiResponse;
import com.fsocial.user.internal.dto.AccountRegisterRequest;
import com.fsocial.user.internal.dto.DuplicationRequest;
import com.fsocial.user.internal.dto.AccountResponse;
import com.fsocial.user.internal.dto.AccountStatisticRegisterDTO;
import com.fsocial.user.internal.dto.AccountStatisticRegisterLongDateDTO;
import com.fsocial.user.internal.dto.DuplicationResponse;
import com.fsocial.user.internal.dto.ManageUserResponse;
import com.fsocial.shared.response.SearchPageResponse;
import com.fsocial.user.internal.entity.Account;
import com.fsocial.auth.internal.entity.AuthProviderCredential;
import com.fsocial.auth.internal.entity.RefreshToken;
import com.fsocial.auth.internal.entity.Token;
import com.fsocial.auth.api.AuthProvider;
import com.fsocial.shared.redis.RedisKeyType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.user.internal.mapper.AccountMapper;
import com.fsocial.user.internal.repository.AccountRepository;
import com.fsocial.auth.internal.repository.AuthProviderRepository;
import com.fsocial.auth.internal.repository.RefreshTokenRepository;
import com.fsocial.user.internal.rbac.RoleRepository;
import com.fsocial.auth.internal.repository.TokenRepository;
import com.fsocial.user.internal.service.AccountService;
import com.fsocial.auth.internal.service.BanService;
import com.fsocial.auth.internal.service.OtpService;
import com.fsocial.user.internal.config.DefaultMediaProvider;
import com.fsocial.user.internal.util.DisplayNameUtils;
import jodd.exception.UncheckedException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class AccountServiceImpl implements AccountService, UserApi {

    AccountRepository accountRepository;
    AuthProviderRepository authProviderRepository;
    FollowService followService;
    RoleRepository roleRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    OtpService otpService;
    BanService banService;
    TokenRepository tokenRepository;
    RefreshTokenRepository refreshTokenRepository;
    DefaultMediaProvider defaultMediaProvider;

    static String DEFAULT_ROLE = "USER";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountResponse persistAccount(AccountRegisterRequest request) {
        Account account = saveAccount(request);
//        createProfile(account, request);
        otpService.deleteOtp(request.getEmail(), RedisKeyType.REGISTER.getRedisKeyPrefix());
        return toAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getUser(String id) {
        return accountRepository.findById(id)
                .map(this::toAccountResponse)
                .orElseThrow(() -> new AppException(StatusCode.ACCOUNT_NOT_EXISTED));
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(StatusCode.ACCOUNT_NOT_EXISTED));

        AuthProviderCredential localAuth = authProviderRepository
                .findByAccount_IdAndProvider(account.getId(), AuthProvider.LOCAL)
                .orElseThrow(() -> new AppException(StatusCode.LOCAL_AUTH_NOT_FOUND));

        localAuth.setPassword(passwordEncoder.encode(newPassword));
        authProviderRepository.save(localAuth);
        log.info("Đặt lại mật khẩu thành công.");
    }

    @Override
    public ApiResponse<DuplicationResponse> checkDuplication(DuplicationRequest request) {
        boolean usernameExisted = accountRepository.countByUsername(request.getUsername()) > 0;
        boolean emailExisted = accountRepository.countByEmail(request.getEmail()) > 0;

        DuplicationResponse response = DuplicationResponse.builder()
                .username(usernameExisted ? StatusCode.USERNAME_EXISTED.getMessage() : null)
                .email(emailExisted ? StatusCode.EMAIL_EXISTED.getMessage() : null)
                .build();

        boolean hasError = usernameExisted || emailExisted;

        return ApiResponse.<DuplicationResponse>builder()
                .statusCode(hasError ? StatusCode.DUPLICATION.getCode() : StatusCode.VALID.getCode())
                .message(hasError ? StatusCode.DUPLICATION.getMessage() : StatusCode.VALID.getMessage())
                .data(hasError ? response : null)
                .build();
    }

    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(StatusCode.ACCOUNT_NOT_EXISTED));

        AuthProviderCredential localAuth = authProviderRepository
                .findByAccount_IdAndProvider(account.getId(), AuthProvider.LOCAL)
                .orElseThrow(() -> new AppException(StatusCode.LOCAL_AUTH_NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, localAuth.getPassword())) {
            throw new AppException(StatusCode.WRONG_PASSWORD);
        }

        localAuth.setPassword(passwordEncoder.encode(newPassword));
        authProviderRepository.save(localAuth);
        log.info("Đổi mật khẩu thành công.");
    }

    @Override
    public boolean existsById(String id) {
        return accountRepository.findById(id).isPresent();
    }

    @Override
    public List<AccountStatisticRegisterDTO> countByCreatedAtByHours(LocalDateTime startDay, LocalDateTime endDay) {
        List<AccountRepository.HourCountResult> results = accountRepository.countByCreatedAtByHours(startDay, endDay);

        Map<Integer, Integer> map = new HashMap<>();
        for (AccountRepository.HourCountResult row : results) {
            if (row._id() != null) map.put(row._id(), row.count() != null ? row.count() : 0);
        }

        List<AccountStatisticRegisterDTO> res = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            res.add(new AccountStatisticRegisterDTO(String.format("%02d:00", hour), map.getOrDefault(hour, 0)));
        }
        return res;
    }

    @Override
    public List<AccountStatisticRegisterLongDateDTO> countByCreatedAtByStartEnd(LocalDateTime startDay, LocalDateTime endDay) {
        List<AccountRepository.DateCountResult> results = accountRepository.countByCreatedAtByDate(startDay, endDay);

        Map<String, Long> dateCountMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (AccountRepository.DateCountResult row : results) {
            if (row._id() != null) dateCountMap.put(row._id(), row.count() != null ? row.count() : 0L);
        }

        List<AccountStatisticRegisterLongDateDTO> res = new ArrayList<>();
        LocalDate start = startDay.toLocalDate();
        LocalDate end = endDay.toLocalDate();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            String dateStr = current.format(formatter);
            Date date = Date.from(current.atStartOfDay(ZoneId.systemDefault()).toInstant());
            res.add(new AccountStatisticRegisterLongDateDTO(date, dateCountMap.getOrDefault(dateStr, 0L)));
            current = current.plusDays(1);
        }
        return res;
    }

    @Override
    @Transactional
    public String banUser(String userId) {
        Account banAccount = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(StatusCode.ACCOUNT_NOT_EXISTED));

        Optional<Token> tokenAccount = tokenRepository.findByAccount(banAccount);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findFirstByAccountIdOrderByExpiryDateDesc(banAccount.getId());

        banAccount.setStatus(false);
        accountRepository.save(banAccount);

        tokenAccount.ifPresent(token -> banService.ban(token.getToken()));
        refreshToken.ifPresent(refresh -> refreshTokenRepository.deleteByToken(refresh.getToken()));

        return "Ban account: " + banAccount.getUsername() + " successful";
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getProfile(String userId) {

        Optional<Account> account = Optional.of(accountRepository.findById(userId).orElseThrow(() -> new AppException(StatusCode.COMMENT_NOT_FOUND)));
        return toAccountResponse(account.get());
    }

    @Override
    public ActorSnapshotDTO getOwner(String userId) {
        Optional<ActorSnapshotDTO> owner = accountRepository.findOwnerById(userId);
        if (owner.isEmpty()) {
            throw new AppException(StatusCode.USER_NOT_FOUND);
        }
        return owner.get();
    }

    @Override
    public List<ActorSnapshotDTO> getOwners(List<String> userIds) {
        return accountRepository.findOwnersByIdIn(userIds);
    }

    @Override
    public boolean canView(String ownerId, String requesterId) {
        Account owner = accountRepository.findById(ownerId).orElse(null);
        return owner == null
                || Boolean.TRUE.equals(owner.getIsPublic())
                || ownerId.equals(requesterId)
                || followService.isFollowing(requesterId, ownerId);
    }

    @Override
    public List<String> findFolloweeIds(String userId) {
        return followService.findFolloweeIds(userId);
    }

    @Override
    public boolean isFollowing(String userId, String targetId) {
        return followService.isFollowing(userId, targetId);
    }

    @Override
    public List<ManageUserResponse> getAllUsers() {
        return accountRepository.findAll().stream()
                .map(account -> ManageUserResponse.builder()
                        .id(account.getId())
                        .username(account.getUsername())
                        .displayName(DisplayNameUtils.build(account))
                        .email(account.getEmail())
                        .createdAt(account.getCreatedAt())
                        .updatedAt(account.getUpdatedAt())
                        .status(account.isStatus())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SearchPageResponse<AccountResponse> searchUsers(String keyword, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(50, Math.max(1, size));
        String trimmedKeyword = keyword == null ? "" : keyword.trim();

        if (trimmedKeyword.isBlank()) {
            return new SearchPageResponse<>(List.of(), safePage, safeSize, false);
        }

        List<Account> accounts = accountRepository.searchByKeyword(
                trimmedKeyword,
                PageRequest.of(safePage, safeSize + 1)
        );

        boolean hasMore = accounts.size() > safeSize;
        List<AccountResponse> items = accounts.stream()
                .limit(safeSize)
                .map(this::toAccountResponse)
                .toList();

        return new SearchPageResponse<>(items, safePage, safeSize, hasMore);
    }

    private Account saveAccount(AccountRegisterRequest request) {
        Account account = accountMapper.toEntity(request);
        account.setCreatedAt(LocalDateTime.now());
        account.setRole(roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new AppException(StatusCode.ROLE_NOT_FOUND)));
        account.setStatus(true);

        String seed = request.getUsername() != null ? request.getUsername() : request.getEmail();
        account.setAvatar(defaultMediaProvider.pickAvatar(seed));
        account.setBackground(defaultMediaProvider.pickBackground(seed));

        Account savedAccount = accountRepository.save(account);

        authProviderRepository.save(AuthProviderCredential.builder()
                .account(savedAccount)
                .provider(AuthProvider.LOCAL)
                .password(passwordEncoder.encode(request.getPassword()))
                .build());

        return savedAccount;
    }

    private AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .dob(account.getDob() != null ? account.getDob().toString() : null)
                .gender(account.getGender())
                .address(account.getAddress())
                .displayName(DisplayNameUtils.build(account))
                .avatar(account.getAvatar())
                .background(account.getBackground())
                .isKOL(account.isKOL())
                .role(account.getRole().getName())
                .bio(account.getBio())
                .follower(new HashSet<>(followService.getFollowers(account.getId())))
                .following(new HashSet<>(followService.getFollowing(account.getId())))
                .build();
    }
}
