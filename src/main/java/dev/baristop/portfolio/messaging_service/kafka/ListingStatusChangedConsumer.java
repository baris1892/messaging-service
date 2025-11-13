package dev.baristop.portfolio.messaging_service.kafka;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import dev.baristop.portfolio.messaging_service.notification.NotificationFactory;
import dev.baristop.portfolio.messaging_service.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ListingStatusChangedConsumer {

    private final NotificationFactory notificationFactory;

    @SneakyThrows
    @KafkaListener(
        topics = KafkaConsumerConfig.TOPIC_LISTING_STATUS_CHANGED,
        groupId = "listing-status-changed-group-email",
        containerFactory = "listingStatusChangedKafkaListenerContainerFactory"
    )
    public void consume(ListingStatusChangedEvent event) {
        log.info("Received ListingStatusChangedEvent: {}", event);

        if ("fail".equalsIgnoreCase(event.getListingTitle())) {
            log.info("Throwing Test Exception on purpose");
            throw new Exception("Test Exception on purpose");
        }

        // send notifications to all channels
        List<NotificationType> channels = List.of(NotificationType.EMAIL, NotificationType.SLACK);
        for (NotificationType type : channels) {
            notificationFactory.getNotificationService(type).notifyListingStatusChanged(event);
        }

        log.info("Finished processing ListingStatusChangedEvent: {}", event);
    }

    @KafkaListener(
        topics = KafkaConsumerConfig.TOPIC_LISTING_STATUS_CHANGED_DLT,
        groupId = "listing-status-changed-group-email-dlt",
        containerFactory = "listingStatusChangedKafkaListenerContainerFactory"
    )
    public void consumeDLT(ListingStatusChangedEvent listingStatusChangedEvent) {
        log.warn("DLT received: {}", listingStatusChangedEvent);

        // do some stuff with the failed listingStatusChangedEvent
    }
}
