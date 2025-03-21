package com.example.cqrs.command.dto.read

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity
import com.example.cqrs.command.entity.event.enumerate.EventStatus
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
        fun from(event: AbstractAccountEventEntity): AccountTransactionResponse {
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
