package com.example.cqrs.infrastructure.eventstore.entity.event.money

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.OperationType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 계좌 잔액 변경 실패 이벤트 (예: 잔액 부족)
 */
@DiscriminatorValue("BalanceChangeFailedEvent")
@Entity
class BalanceChangeFailedEvent(
    accountId: String,
    amount: Double,

    @Column(nullable = false)
    val reason: String,

    @Column(nullable = false)
    val operationType: OperationType,

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
            reason: String,
            operationType: OperationType,
            eventDate: LocalDateTime,
            metadata: EventMetadata
        ): BalanceChangeFailedEvent {
            return BalanceChangeFailedEvent(
                accountId = accountId,
                amount = amount,
                reason = reason,
                operationType = operationType,
                eventDate = eventDate,
                metadata = metadata
            )
        }
    }
}
