package com.fsocial.notification.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.notification.internal.dto.DeviceTokenDTO;
import com.fsocial.notification.internal.dto.NoticeRequest;
import com.fsocial.notification.internal.dto.NotificationCursorResponse;
import com.fsocial.notification.internal.dto.NotificationResponse;
import com.fsocial.notification.internal.service.DemoNotificationService;
import com.fsocial.notification.internal.service.DeviceTokenService;
import com.fsocial.notification.internal.service.NotificaitonService;
import com.fsocial.notification.internal.service.FcmServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/notification")
public class NotificationController {

    NotificaitonService notificationService;
    FcmServiceImpl fcmService;
    DeviceTokenService tokenService;
    DemoNotificationService demoNotificationService;

    @PostMapping("/register-token")
    public ApiResponse<Void> registerToken(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody DeviceTokenDTO req
    ) {
        tokenService.registerToken(jwt.getSubject(), req.token(), req.deviceType());
        return ApiResponse.<Void>builder()
                .message("Register token success")
                .build();
    }

//    @PostMapping("/send/{userId}")
//    public ResponseEntity<ApiResponse<String>> sendToUser(
//            @PathVariable String userId,
//            @RequestBody NotificationDTO req) throws FirebaseMessagingException {
//        List<String> tokens = tokenService.getTokenByUserId(userId);
//        if (tokens.isEmpty()) {
//            return ResponseEntity.status(404).body(ApiResponse.<String>builder()
//                    .statusCode(404)
//                    .message("Device token not found")
//                    .build());
//        }
//
//        BatchResponse res = fcmService.sendToMultipleTokens(
//                tokens, req.title(), req.body(), req.data());
//        return ResponseEntity.ok(ApiResponse.<String>builder()
//                .data("Sent: " + res.getSuccessCount())
//                .message("Send notification success")
//                .build());
//    }

    @GetMapping
    public ApiResponse<NotificationCursorResponse> getNotifications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String cursor) {
        return ApiResponse.<NotificationCursorResponse>builder()
                .data(notificationService.getNotifications(jwt.getSubject(), cursor))
                .message("Get notifications success")
                .build();
    }

    @GetMapping("/un-read")
    public ApiResponse<Long> getNotificationsUnRead(
            @AuthenticationPrincipal Jwt jwt
    ){
        return ApiResponse.<Long>builder()
                .data(notificationService.getCountNotificationByRecipientId(jwt.getSubject()))
                .message("Get notification un read success")
                .build();
    }

    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(@RequestBody NoticeRequest notificationRequest) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.createNotification(notificationRequest))
                .message("Create notification success")
                .build();
    }
}
