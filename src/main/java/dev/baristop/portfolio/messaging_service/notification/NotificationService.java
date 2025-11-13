package dev.baristop.portfolio.messaging_service.notification;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;

public interface NotificationService {
    void notifyListingStatusChanged(ListingStatusChangedEvent event);
}
