package dev.baristop.portfolio.messaging_service.email;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final EmailService emailService;

    public void handleListingStatusChange(ListingStatusChangedEvent event) {
        String subject = buildSubject(event);
        String body = buildBody(event);

        emailService.sendPlainTextEmail(event.getRecipientEmail(), subject, body);
        log.info("Email sent to {} with subject '{}'", event.getRecipientEmail(), subject);
    }

    private String buildSubject(ListingStatusChangedEvent event) {
        return switch (event.getStatus()) {
            case APPROVED -> "Your listing was approved ✅";
            case REJECTED -> "Your listing was rejected ❌";
            default -> "Listing status updated";
        };
    }

    private String buildBody(ListingStatusChangedEvent event) {
        return switch (event.getStatus()) {
            case APPROVED -> String.format(
                "Hi, good news! Your listing \"%s\" has been approved and is now live.%n%n– The Portfolio Team",
                event.getListingTitle()
            );
            case REJECTED -> String.format(
                "Hi, unfortunately, your listing \"%s\" was rejected.%n%n– The Portfolio Team",
                event.getListingTitle()
            );
            default -> String.format(
                "Hi, your listing \"%s\" changed status to %s.%n%n– The Portfolio Team",
                event.getListingTitle(),
                event.getStatus()
            );
        };
    }
}
