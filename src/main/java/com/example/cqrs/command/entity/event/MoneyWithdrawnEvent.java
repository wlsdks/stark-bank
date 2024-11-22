package com.example.cqrs.command.entity.event;

import com.example.cqrs.command.entity.event.metadata.EventMetadata;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MoneyWithdrawnEvent")
@Entity
public class MoneyWithdrawnEvent extends AbstractAccountEvent {

    public MoneyWithdrawnEvent(String accountId, LocalDateTime eventDate,
                              double amount, EventMetadata metadata) {
        super(accountId, eventDate, amount, metadata);
    }

}