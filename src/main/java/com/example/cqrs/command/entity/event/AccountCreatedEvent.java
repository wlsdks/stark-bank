package com.example.cqrs.command.entity.event;

import com.example.cqrs.command.entity.event.metadata.EventMetadata;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("AccountCreatedEvent")
@Entity
public class AccountCreatedEvent extends AbstractAccountEvent {

    public AccountCreatedEvent(String accountId, LocalDateTime eventDate,
                             double amount, EventMetadata metadata) {
        super(accountId, eventDate, amount, metadata);
    }

    // factory method
    public static AccountCreatedEvent of(String accountId, LocalDateTime eventDate, double amount, EventMetadata metadata) {
        return new AccountCreatedEvent(accountId, eventDate, amount, metadata);
    }

}
