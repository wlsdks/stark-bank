package com.example.cqrs.infrastructure.eventstore.entity.event.money

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 계좌 이체 입금 이벤트
 */
@DiscriminatorValue("MoneyTransferredInEvent")
@Entity
class MoneyTransferredInEventEntity(
    accountId: String,
    amount: Double,

    @Column(nullable = false)
    val sourceAccountId: String,

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
            sourceAccountId: String,
            amount: Double,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): MoneyTransferredInEventEntity {
            return MoneyTransferredInEventEntity(
                accountId = accountId,
                sourceAccountId = sourceAccountId,
                amount = amount,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}