package dev.baristop.portfolio.messaging_service.notification.slack;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import dev.baristop.portfolio.messaging_service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SlackNotificationService implements NotificationService {

    @Override
    public void notifyListingStatusChanged(ListingStatusChangedEvent event) {
        // Example: Send message to Slack webhook
        log.info("Slack message sent for event: {}", event);
    }
}
