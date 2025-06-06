package com.example.cqrs.infrastructure.eventstore.entity.event.money

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 입금 이벤트
 */
@DiscriminatorValue("MoneyDepositedEvent")
@Entity
class MoneyDepositedEventEntity(
    accountId: String,
    amount: Double,
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
            amount: Double,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): MoneyDepositedEventEntity {
            return MoneyDepositedEventEntity(
                accountId = accountId,
                amount = amount,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}