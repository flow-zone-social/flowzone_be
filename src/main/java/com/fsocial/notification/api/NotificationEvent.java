package com.fsocial.notification.api;

import com.fsocial.notification.internal.dto.NotificationDTO;
import com.fsocial.shared.messaging.AMQPConst;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEvent {

    RabbitTemplate rabbitTemplate;

    public void publishCreateNotification(NotificationDTO notiDTO) {
        rabbitTemplate.convertAndSend(AMQPConst.CREATE_NOTIFICATION.getExchange(), AMQPConst.CREATE_NOTIFICATION.getRoutingKey(), notiDTO);
    }
}
