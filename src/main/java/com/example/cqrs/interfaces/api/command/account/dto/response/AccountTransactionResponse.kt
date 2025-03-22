package com.example.cqrs.interfaces.api.command.account.dto.response

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "계좌 거래 응답")
data class AccountTransactionResponse(
    @Schema(description = "계좌 ID", example = "ACC-20250322-001")
    val accountId: String,

    @Schema(description = "이벤트 유형 (예: 입금, 출금, 이체)", example = "MoneyDepositedEvent")
    val eventType: String,

    @Schema(description = "거래 금액", example = "100000.00")
    val amount: Double,

    @Schema(description = "이벤트 발생 일시", example = "2025-03-22T14:30:15")
    val eventDate: LocalDateTime,

    @Schema(description = "이벤트 처리 상태", example = "PROCESSED")
    val status: EventStatus,

    @Schema(description = "이벤트 메타데이터 정보")
    val metadata: EventMetadataResponse
) {
    companion object {
        fun from(event: AccountEventBaseEntity): AccountTransactionResponse {
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