package com.example.cqrs.query.dto;

import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import com.example.cqrs.command.entity.event.enumerate.EventStatus;
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

    private String accountId;               // 계좌 ID
    private String eventType;               // 이벤트 타입
    private Double amount;                  // 거래 금액
    private LocalDateTime eventDate;        // 이벤트 발생 일시
    private EventStatus status;             // 이벤트 상태
    private EventMetadataResponse metadata; // 이벤트 메타데이터

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
