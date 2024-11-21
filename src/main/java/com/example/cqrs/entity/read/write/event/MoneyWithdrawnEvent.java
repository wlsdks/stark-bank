package com.example.cqrs.entity.read.write.event;

import event_sourcing.study.entity.write.event.base.BaseAccountEvent;
import event_sourcing.study.entity.write.event.base.EventMetadata;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MoneyWithdrawnEvent")
@Entity
public class MoneyWithdrawnEvent extends BaseAccountEvent {

    public MoneyWithdrawnEvent(String accountId, LocalDateTime eventDate,
                              double amount, EventMetadata metadata) {
        super(accountId, eventDate, amount, metadata);
    }

}