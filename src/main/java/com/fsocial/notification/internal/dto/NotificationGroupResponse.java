package com.fsocial.notification.internal.dto;

import java.time.LocalDate;
import java.util.List;

public record NotificationGroupResponse(
        LocalDate date,
        List<NotificationResponse> notifications
) {
}
