package com.fsocial.notification.internal.consumer;

import com.fsocial.notification.internal.dto.NotificationDTO;
import com.fsocial.notification.internal.service.NotificaitonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    NotificaitonService notificaitonService;

    @RabbitListener(queues = "#{@createNotificationQueue.name}")
    public void receiveCreateNotification(NotificationDTO notiDTO) {
        notificaitonService.notifcationCreateConsumer(notiDTO);
    }
}
