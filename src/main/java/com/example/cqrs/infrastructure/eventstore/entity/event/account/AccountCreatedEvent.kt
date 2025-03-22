package com.example.cqrs.infrastructure.eventstore.entity.event.account

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

@DiscriminatorValue("AccountCreatedEvent")
@Entity
class AccountCreatedEvent(
    accountId: String,
    amount: Double? = 0.0,
    eventDate: LocalDateTime,
    metadata: EventMetadata
) : AccountEvent(
    id = null,
    accountId = accountId,
    amount = amount,
    eventDate = eventDate,
    metadata = metadata
) {
    companion object {
        fun of(
            accountId: String,
            amount: Double = 0.0,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): AccountCreatedEvent {
            return AccountCreatedEvent(
                accountId = accountId,
                amount = amount,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}