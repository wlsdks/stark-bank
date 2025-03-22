package com.example.cqrs.infrastructure.eventstore.entity.event.account

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

@DiscriminatorValue("AccountCreatedEvent")
@Entity
class AccountCreatedEventEntity(
    accountId: String,
    amount: Double? = 0.0,
    eventDate: LocalDateTime,
    metadata: EventMetadata
) : AccountEventBaseEntity(
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
        ): AccountCreatedEventEntity {
            return AccountCreatedEventEntity(
                accountId = accountId,
                amount = amount,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}