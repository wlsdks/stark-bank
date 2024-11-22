package com.example.cqrs.common.dto;

import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountEventDto {

    private String eventType;
    private String accountId;
    private LocalDateTime eventDate;
    private Double amount;
    private String userId;
    private String correlationId;
    
    public static AccountEventDto from(AbstractAccountEvent event) {
        return new AccountEventDto(
            event.getClass().getSimpleName(),
            event.getAccountId(),
            event.getEventDate(),
            event.getAmount(),
            event.getMetadata().getUserId(),
            event.getMetadata().getCorrelationId()
        );
    }

}