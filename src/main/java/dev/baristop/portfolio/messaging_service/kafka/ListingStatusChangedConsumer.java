package dev.baristop.portfolio.messaging_service.kafka;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ListingStatusChangedConsumer {

    @SneakyThrows
    @KafkaListener(
        topics = KafkaConsumerConfig.TOPIC_LISTING_STATUS_CHANGED,
        groupId = "listing-status-changed-group-email",
        containerFactory = "listingStatusChangedKafkaListenerContainerFactory"
    )
    public void consume(ListingStatusChangedEvent listingStatusChangedEvent) {
        log.info("Received ListingStatusChangedEvent: {}", listingStatusChangedEvent);

        if (listingStatusChangedEvent.getListingTitle().equalsIgnoreCase("fail")) {
            log.info("Throwing Test Exception on purpose");
            throw new Exception("Test Exception on purpose");
        }

        log.info("Finished processing ListingStatusChangedEvent: {}", listingStatusChangedEvent);
    }

    // Dead Letter Topic (DLT) listener
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
