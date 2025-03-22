package com.example.cqrs.application.account.command.dto.response

data class AccountDetailResponse(
    val accountId: String,
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
