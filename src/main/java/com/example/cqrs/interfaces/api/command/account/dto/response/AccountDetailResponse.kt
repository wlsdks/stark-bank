package com.example.cqrs.interfaces.api.command.account.dto.response

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
