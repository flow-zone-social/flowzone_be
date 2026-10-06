package com.fsocial.user.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.shared.response.ResponseStatus;
import com.fsocial.user.internal.dto.FollowRequest;
import com.fsocial.user.internal.service.FollowService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/account")
@Slf4j
public class FollowController {

    FollowService followService;

    @PostMapping("/follow")
    public ApiResponse<Void> follow(@RequestBody @Valid FollowRequest request,
                                    @AuthenticationPrincipal Jwt jwt
                                    ) {
        followService.follow(jwt.getSubject(), request.getFollowing());
        return ApiResponse.<Void>builder()
                .statusCode(ResponseStatus.SUCCESS.getCODE())
                .message(ResponseStatus.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/unfollow")
    public ApiResponse<Void> unfollow(@RequestBody @Valid FollowRequest request,
                                      @AuthenticationPrincipal Jwt jwt) {
        followService.unfollow(jwt.getSubject(), request.getFollowing());
        return ApiResponse.<Void>builder()
                .statusCode(ResponseStatus.SUCCESS.getCODE())
                .message(ResponseStatus.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/followers/{userId}")
    public ApiResponse<Set<String>> getFollowers(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<Set<String>>builder()
                .statusCode(ResponseStatus.SUCCESS.getCODE())
                .message(ResponseStatus.SUCCESS.getMessage())
                .data(followService.getFollowers(jwt.getSubject()))
                .build();
    }

    @GetMapping("/followings/{userId}")
    public ApiResponse<Set<String>> getFollowings(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<Set<String>>builder()
                .statusCode(ResponseStatus.SUCCESS.getCODE())
                .message(ResponseStatus.SUCCESS.getMessage())
                .data(followService.getFollowing(jwt.getSubject()))
                .build();
    }

    @PostMapping("/follow-check")
    public ApiResponse<Boolean> checkFollow(@RequestBody @Valid FollowRequest request,
                                    @AuthenticationPrincipal Jwt jwt
    ) {

        return ApiResponse.<Boolean>builder()
                .message(ResponseStatus.SUCCESS.getMessage())
                .data(followService.checkFollow(jwt.getSubject(), request.getFollowing()))
                .build();
    }
}
