package com.example.cqrs.infrastructure.eventstore.event.money

import com.example.cqrs.infrastructure.eventstore.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.base.metadata.EventMetadata
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 계좌 이체 출금 이벤트
 */
@DiscriminatorValue("MoneyTransferredOutEvent")
@Entity
class MoneyTransferredOutEvent(
    accountId: String,
    amount: Double,

    @Column(nullable = false)
    val targetAccountId: String,

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
            targetAccountId: String,
            amount: Double,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): com.example.cqrs.infrastructure.eventstore.event.money.MoneyTransferredOutEvent {
            return com.example.cqrs.infrastructure.eventstore.event.money.MoneyTransferredOutEvent(
                accountId = accountId,
                targetAccountId = targetAccountId,
                amount = amount,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}