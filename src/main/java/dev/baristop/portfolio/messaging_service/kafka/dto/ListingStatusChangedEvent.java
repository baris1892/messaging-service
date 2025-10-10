package dev.baristop.portfolio.messaging_service.kafka.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ListingStatusChangedEvent {
    private ListingStatus status;
    private String recipientEmail;
    private Long listingId;
    private String listingTitle;
    private String listingDescription;
}
