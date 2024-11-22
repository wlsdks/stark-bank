package com.example.cqrs.query.dto;

import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 계좌 거래 이력에 대한 Response DTO입니다.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class AccountTransactionResponse {

    private String accountId;
    private String eventType;
    private Double amount;
    private LocalDateTime eventDate;
    private EventMetadataResponse metadata;

    // factory method
    public static AccountTransactionResponse from(AbstractAccountEvent event) {
        return AccountTransactionResponse.builder()
                .accountId(event.getAccountId())
                .eventType(event.getClass().getSimpleName())
                .amount(event.getAmount())
                .eventDate(event.getEventDate())
                .metadata(EventMetadataResponse.from(event.getMetadata()))
                .build();
    }

}
