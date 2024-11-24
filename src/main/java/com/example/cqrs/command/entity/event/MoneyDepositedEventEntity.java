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
public class MoneyDepositedEventEntity extends AbstractAccountEventEntity {

    public MoneyDepositedEventEntity(String accountId, LocalDateTime eventDate,
                                     double amount, EventMetadata metadata) {
        super(accountId, eventDate, amount, metadata);
    }

    // factory method
    public static MoneyDepositedEventEntity of(String accountId, LocalDateTime now, double amount, EventMetadata eventMetadata) {
        return new MoneyDepositedEventEntity(accountId, now, amount, eventMetadata);
    }

}