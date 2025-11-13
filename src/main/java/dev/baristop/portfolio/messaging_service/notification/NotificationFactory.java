package dev.baristop.portfolio.messaging_service.notification;

import dev.baristop.portfolio.messaging_service.notification.email.EmailNotificationService;
import dev.baristop.portfolio.messaging_service.notification.slack.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final EmailNotificationService emailNotificationService;
    private final SlackNotificationService slackNotificationService;

    public NotificationService getNotificationService(NotificationType type) {
        return switch (type) {
            case NotificationType.EMAIL -> emailNotificationService;
            case NotificationType.SLACK -> slackNotificationService;
        };
    }
}
