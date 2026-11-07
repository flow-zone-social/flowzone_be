package com.fsocial.notification.internal.service;

import com.fsocial.notification.internal.dto.NotificationGroupResponse;
import com.fsocial.notification.api.NotificationType;

import java.util.List;

public interface DemoNotificationService {
//    NotificationResponse create(DemoNotificationRequest request);
    List<NotificationGroupResponse> getByRecipient(String recipientId, NotificationType type, int page);
}
