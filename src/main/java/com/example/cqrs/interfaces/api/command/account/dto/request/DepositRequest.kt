package com.example.cqrs.interfaces.api.command.account.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "입금 요청")
data class DepositRequest(
    @Schema(description = "계좌 ID", example = "A001")
    val accountId: String,

    @Schema(description = "입금 금액", example = "10000")
    val amount: Double,

    @Schema(description = "사용자 ID", example = "U001")
    val userId: String
) {
    companion object {
        fun of(accountId: String, amount: Double, userId: String): DepositRequest {
            return DepositRequest(accountId, amount, userId)
        }
    }
}
