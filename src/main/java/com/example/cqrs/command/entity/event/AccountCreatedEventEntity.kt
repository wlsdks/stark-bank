package com.example.cqrs.command.entity.event

import com.example.cqrs.command.entity.event.event.AbstractAccountEventEntity
import com.example.cqrs.command.entity.event.metadata.EventMetadata
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("AccountCreatedEvent")
class AccountCreatedEventEntity(
    accountId: String,
    eventDate: LocalDateTime,
    amount: Double,
    metadata: EventMetadata
) : AbstractAccountEventEntity(
    id = null,
    accountId = accountId,
    eventDate = eventDate,
    amount = amount,
    metadata = metadata
) {
    companion object {
        fun of(
            accountId: String,
            eventDate: LocalDateTime,
            amount: Double,
            metadata: EventMetadata
        ): AccountCreatedEventEntity {
            return AccountCreatedEventEntity(
                accountId = accountId,
                eventDate = eventDate,
                amount = amount,
                metadata = metadata
            )
        }
    }
}