package com.fsocial.notification.internal.dto;

import java.util.List;

public record NotificationCursorResponse(
        List<NotificationResponse> items,
        String nextCursor,
        boolean hasMore
) {
}
