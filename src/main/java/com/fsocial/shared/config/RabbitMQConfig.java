package com.fsocial.shared.config;

import com.fsocial.notification.internal.dto.NotificationDTO;
import com.fsocial.post.internal.feed.dto.InteractionEvent;
import com.fsocial.shared.messaging.AMQPConst;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RabbitMQConfig {

    // Map cả FQCN cũ (trước khi migrate cấu trúc module) lẫn FQCN mới cho __TypeId__,
    // tránh vỡ deserialize với message cũ còn tồn đọng trong queue lúc deploy.
    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.fsocial.dto.notification.NotificationDTO", NotificationDTO.class);
        idClassMapping.put("com.fsocial.notification.internal.dto.NotificationDTO", NotificationDTO.class);
        idClassMapping.put("com.fsocial.dto.feed.InteractionEvent", InteractionEvent.class);
        idClassMapping.put("com.fsocial.post.internal.feed.dto.InteractionEvent", InteractionEvent.class);
        typeMapper.setIdClassMapping(idClassMapping);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public Queue postCommentAttachmentsQueue() {
        return new Queue(AMQPConst.POST_ATTACHMENTS_DELETE.getQueue(), true);
    }

    @Bean
    public FanoutExchange postDeleteExchange(){
        return new FanoutExchange(AMQPConst.POST_ATTACHMENTS_DELETE.getExchange());
    }

    @Bean
    public Binding postCommentAttachmentsBinding(){
        return BindingBuilder.bind(postCommentAttachmentsQueue()).to(postDeleteExchange());
    }

    // --- Interaction events (BRD Feed Recommendation) ---

    @Bean
    public Queue scoreUpdateQueue() {
        return new Queue(AMQPConst.SCORE_UPDATE.getQueue(), true);
    }

    @Bean
    public Queue interestUpdateQueue() {
        return new Queue(AMQPConst.INTEREST_UPDATE.getQueue(), true);
    }

    @Bean
    public FanoutExchange interactionFanoutExchange() {
        return new FanoutExchange(AMQPConst.SCORE_UPDATE.getExchange());
    }

    @Bean
    public Binding scoreUpdateBinding() {
        return BindingBuilder.bind(scoreUpdateQueue()).to(interactionFanoutExchange());
    }

    @Bean
    public Binding interestUpdateBinding() {
        return BindingBuilder.bind(interestUpdateQueue()).to(interactionFanoutExchange());
    }

    // --- Notification events ---

    @Bean
    public Queue createNotificationQueue() {
        return new Queue(AMQPConst.CREATE_NOTIFICATION.getRoutingKey(), true);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(AMQPConst.CREATE_NOTIFICATION.getExchange());
    }

    @Bean
    public Binding createNotificationBinding() {
        return BindingBuilder.bind(createNotificationQueue())
                .to(notificationExchange())
                .with(AMQPConst.CREATE_NOTIFICATION.getRoutingKey());
    }
}
