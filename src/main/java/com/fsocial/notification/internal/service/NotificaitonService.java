package com.fsocial.notification.internal.service;

import com.fsocial.notification.internal.dto.NoticeRequest;
import com.fsocial.notification.internal.dto.NotificationDTO;
import com.fsocial.notification.internal.dto.NotificationCursorResponse;
import com.fsocial.notification.internal.dto.NotificationResponse;

public interface NotificaitonService {
    NotificationResponse createNotification(NoticeRequest notificationRequest);
    void notifcationCreateConsumer (NotificationDTO dto) ;
    NotificationCursorResponse getNotifications(String userId, String cursor);
    long getCountNotificationByRecipientId(String recipient);
    NotificationResponse readNotification(String notificationId);
}
