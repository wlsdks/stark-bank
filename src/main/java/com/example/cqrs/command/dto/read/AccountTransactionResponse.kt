package com.example.cqrs.command.dto.read

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
        fun from(
            accountId: String,
            eventType: String,
            amount: Double,
            eventDate: LocalDateTime,
            status: EventStatus,
            metadata: EventMetadataResponse
        ): AccountTransactionResponse {
            return AccountTransactionResponse(accountId, eventType, amount, eventDate, status, metadata)
        }
    }
}
