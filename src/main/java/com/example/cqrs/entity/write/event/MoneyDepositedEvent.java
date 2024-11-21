package com.example.cqrs.entity.write.event;

import com.example.cqrs.entity.write.event.base.BaseAccountEvent;
import com.example.cqrs.entity.write.event.base.EventMetadata;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MoneyDepositedEvent")
@Entity
public class MoneyDepositedEvent extends BaseAccountEvent {

    public MoneyDepositedEvent(String accountId, LocalDateTime eventDate,
                              double amount, EventMetadata metadata) {
        super(accountId, eventDate, amount, metadata);
    }

}