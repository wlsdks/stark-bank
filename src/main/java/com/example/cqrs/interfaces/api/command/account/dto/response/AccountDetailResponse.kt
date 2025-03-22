package com.example.cqrs.interfaces.api.command.account.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "계좌 상세 응답")
data class AccountDetailResponse(
    @Schema(description = "계좌 ID", example = "A001")
    val accountId: String,

    @Schema(description = "잔액", example = "10000")
    val balance: Double
) {
    companion object {
        fun from(
            accountId: String,
            balance: Double
        ): AccountDetailResponse {
            return AccountDetailResponse(accountId, balance)
        }
    }
}
