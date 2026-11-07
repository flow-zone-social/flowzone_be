package com.fsocial.post.internal.feed.publisher;

import com.fsocial.post.internal.feed.dto.InteractionEvent;
import com.fsocial.shared.messaging.AMQPConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InteractionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String postId, String userId, String actionType, List<String> postTags) {
        try {
            InteractionEvent event = InteractionEvent.builder()
                    .postId(postId)
                    .userId(userId)
                    .actionType(actionType)
                    .postTags(postTags != null ? postTags : List.of())
                    .timestamp(LocalDateTime.now())
                    .build();
            rabbitTemplate.convertAndSend(AMQPConst.SCORE_UPDATE.getExchange(), "", event);
            log.debug("Published interaction event: postId={}, userId={}, action={}", postId, userId, actionType);
        } catch (Exception e) {
            log.warn("Failed to publish interaction event for post {}: {}", postId, e.getMessage());
        }
    }
}
