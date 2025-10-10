package dev.baristop.portfolio.messaging_service.email;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatus;
import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailNotificationServiceTest {

    private static final String RECIPIENT_EMAIL = "user@example.com";
    private static final String DESCRIPTION = "desc";

    private EmailService emailService;
    private EmailNotificationService emailNotificationService;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        emailNotificationService = new EmailNotificationService(emailService);
    }

    @Test
    void shouldSendApprovedEmail() {
        ListingStatusChangedEvent event = createEvent(ListingStatus.APPROVED, "Test Listing", 1L);

        emailNotificationService.handleListingStatusChange(event);

        var captured = captureSentEmail();

        assertThat(captured.subject).isEqualTo("Your listing was approved ✅");
        assertThat(captured.body)
            .contains("Test Listing")
            .contains("approved");
    }

    @Test
    void shouldSendRejectedEmail() {
        ListingStatusChangedEvent event = createEvent(ListingStatus.REJECTED, "My Listing", 2L);

        emailNotificationService.handleListingStatusChange(event);

        var captured = captureSentEmail();

        assertThat(captured.subject).isEqualTo("Your listing was rejected ❌");
        assertThat(captured.body)
            .contains("My Listing")
            .contains("rejected");
    }

    @Test
    void shouldSendGenericEmailForOtherStatus() {
        ListingStatusChangedEvent event = createEvent(ListingStatus.PENDING, "Another Listing", 3L);

        emailNotificationService.handleListingStatusChange(event);

        var captured = captureSentEmail();

        assertThat(captured.subject).isEqualTo("Listing status updated");
        assertThat(captured.body)
            .contains("Another Listing")
            .contains("PENDING");
    }

    private ListingStatusChangedEvent createEvent(ListingStatus status, String title, Long id) {
        return new ListingStatusChangedEvent(status, RECIPIENT_EMAIL, id, title, DESCRIPTION);
    }

    private CapturedEmail captureSentEmail() {
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendPlainTextEmail(eq(RECIPIENT_EMAIL), subjectCaptor.capture(), bodyCaptor.capture());

        return new CapturedEmail(subjectCaptor.getValue(), bodyCaptor.getValue());
    }

    private record CapturedEmail(String subject, String body) {
    }
}
