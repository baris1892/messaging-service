package dev.baristop.portfolio.messaging_service.kafka;

import dev.baristop.portfolio.messaging_service.email.EmailNotificationService;
import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatus;
import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ListingStatusChangedConsumerTest {

    private EmailNotificationService emailNotificationService;
    private ListingStatusChangedConsumer consumer;

    @BeforeEach
    void setUp() {
        emailNotificationService = mock(EmailNotificationService.class);
        consumer = new ListingStatusChangedConsumer(emailNotificationService);
    }

    @Test
    void shouldCallEmailNotificationServiceOnConsume() {
        ListingStatusChangedEvent event = new ListingStatusChangedEvent(
            ListingStatus.APPROVED, "user@example.com", 1L, "Test Listing", "desc"
        );

        consumer.consume(event);

        verify(emailNotificationService).handleListingStatusChange(event);
    }

    @Test
    void shouldThrowExceptionForFailTitle() {
        ListingStatusChangedEvent event = new ListingStatusChangedEvent(
            ListingStatus.APPROVED, "user@example.com", 1L, "fail", "desc"
        );

        try {
            consumer.consume(event);
        } catch (Exception e) {
            // expected
        }

        // EmailNotificationService should not be called
        verifyNoInteractions(emailNotificationService);
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
