package com.fsocial.notification.internal.dto;

public record DeviceTokenDTO(
        String userId,
        String token,
        String deviceType
) {}
