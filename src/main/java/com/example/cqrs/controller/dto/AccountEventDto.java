package com.example.cqrs.controller.dto;

import com.example.cqrs.entity.write.event.base.BaseAccountEvent;
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
    
    public static AccountEventDto from(BaseAccountEvent event) {
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