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
public class AccountCreatedEventEntity extends AbstractAccountEventEntity {

    public AccountCreatedEventEntity(String accountId, LocalDateTime eventDate,
                                     double amount, EventMetadata metadata) {
        super(accountId, eventDate, amount, metadata);
    }

    // factory method
    public static AccountCreatedEventEntity of(String accountId, LocalDateTime eventDate, double amount, EventMetadata metadata) {
        return new AccountCreatedEventEntity(accountId, eventDate, amount, metadata);
    }

}
