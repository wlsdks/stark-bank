package com.example.cqrs.infrastructure.eventstore.event.money

import com.example.cqrs.infrastructure.eventstore.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.base.metadata.EventMetadata
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 입금 이벤트
 */
@DiscriminatorValue("MoneyDepositedEvent")
@Entity
class MoneyDepositedEvent(
    accountId: String,
    amount: Double,
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
            amount: Double,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): com.example.cqrs.infrastructure.eventstore.event.money.MoneyDepositedEvent {
            return com.example.cqrs.infrastructure.eventstore.event.money.MoneyDepositedEvent(
                accountId = accountId,
                amount = amount,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}