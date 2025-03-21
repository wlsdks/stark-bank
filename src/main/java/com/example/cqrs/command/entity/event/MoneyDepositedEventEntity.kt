package com.example.cqrs.command.entity.event

import com.example.cqrs.command.entity.event.event.AbstractAccountEventEntity
import com.example.cqrs.command.entity.event.metadata.EventMetadata
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("MoneyDepositedEvent")
class MoneyDepositedEventEntity(
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
        // factory method
        fun of(
            accountId: String,
            now: LocalDateTime,
            amount: Double,
            eventMetadata: EventMetadata
        ): MoneyDepositedEventEntity {
            return MoneyDepositedEventEntity(accountId, now, amount, eventMetadata)
        }
    }
}