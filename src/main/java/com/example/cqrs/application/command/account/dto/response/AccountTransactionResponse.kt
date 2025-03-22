package com.example.cqrs.application.command.account.dto.response

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventStatus
import java.time.LocalDateTime

data class AccountTransactionResponse(
    val accountId: String,
    val eventType: String,
    val amount: Double,
    val eventDate: LocalDateTime,
    val status: EventStatus,
    val metadata: EventMetadataResponse
) {
    companion object {
        fun from(event: AccountEvent): AccountTransactionResponse {
            return AccountTransactionResponse(
                accountId = event.accountId,
                eventType = event.javaClass.simpleName,
                amount = event.amount ?: 0.0,
                eventDate = event.eventDate,
                status = event.status,
                metadata = EventMetadataResponse.from(event.metadata)
            )
        }
    }
}
