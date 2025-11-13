package dev.baristop.portfolio.messaging_service.kafka;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatus;
import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import dev.baristop.portfolio.messaging_service.notification.NotificationFactory;
import dev.baristop.portfolio.messaging_service.notification.NotificationService;
import dev.baristop.portfolio.messaging_service.notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ListingStatusChangedConsumerTest {

    private NotificationService notificationService;
    private ListingStatusChangedConsumer consumer;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        NotificationFactory notificationFactory = mock(NotificationFactory.class);

        when(notificationFactory.getNotificationService(any(NotificationType.class)))
            .thenReturn(notificationService);

        consumer = new ListingStatusChangedConsumer(notificationFactory);
    }

    @Test
    void shouldCallNotificationServiceOnConsume() {
        ListingStatusChangedEvent event = new ListingStatusChangedEvent(
            ListingStatus.APPROVED, "user@example.com", 1L, "Test Listing", "desc"
        );

        consumer.consume(event);

        verify(notificationService, times(2)).notifyListingStatusChanged(event);
    }

    @Test
    void shouldThrowExceptionForFailTitle() {
        ListingStatusChangedEvent event = new ListingStatusChangedEvent(
            ListingStatus.APPROVED, "user@example.com", 1L, "fail", "desc"
        );

        try {
            consumer.consume(event);
        } catch (Exception e) {
            // expected exception
        }

        // EmailNotificationService should not be called
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldHandleDLTConsume() {
        ListingStatusChangedEvent event = new ListingStatusChangedEvent(
            ListingStatus.REJECTED, "user@example.com", 2L, "Some Listing", "desc"
        );

        consumer.consumeDLT(event);

        // nothing to verify because method only logs
        // just check it doesn’t throw
    }
}
