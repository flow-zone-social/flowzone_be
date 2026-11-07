package com.fsocial.notification.internal.dto;

import com.fsocial.notification.api.NotificationType;

public record NotificationDTO(
         String recipientId,

         String senderId,

         NotificationType type
){}
