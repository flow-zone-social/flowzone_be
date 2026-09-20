package com.fsocial.notification.internal.repository;

import com.fsocial.notification.internal.entity.NotificationTemplate;
import com.fsocial.notification.api.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {

    Optional<NotificationTemplate> findByType(NotificationType type);
}
