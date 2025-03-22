package com.example.cqrs.interfaces.api.command.account.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이체 요청")
data class TransferRequest(
    @Schema(description = "출금 계좌 ID", example = "A001")
    val fromAccountId: String,

    @Schema(description = "입금 계좌 ID", example = "A002")
    val toAccountId: String,

    @Schema(description = "이체 금액", example = "10000")
    val amount: Double,

    @Schema(description = "사용자 ID", example = "U001")
    val userId: String
) {
    companion object {
        fun of(fromAccountId: String, toAccountId: String, amount: Double, userId: String): TransferRequest {
            return TransferRequest(fromAccountId, toAccountId, amount, userId)
        }
    }
}
