package com.example.cqrs.command.entity.event;

import com.example.cqrs.command.entity.event.metadata.EventMetadata;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MoneyDepositedEvent")
@Entity
public class MoneyDepositedEvent extends AbstractAccountEvent {

    public MoneyDepositedEvent(String accountId, LocalDateTime eventDate,
                               double amount, EventMetadata metadata) {
        super(accountId, eventDate, amount, metadata);
    }

    // factory method
    public static MoneyDepositedEvent of(String accountId, LocalDateTime now, double amount, EventMetadata eventMetadata) {
        return new MoneyDepositedEvent(accountId, now, amount, eventMetadata);
    }

}