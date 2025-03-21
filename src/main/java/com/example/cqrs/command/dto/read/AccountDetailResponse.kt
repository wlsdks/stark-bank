package com.example.cqrs.command.dto.read

data class AccountDetailResponse(
    val accountId: String,
    val balance: Double
) {
    companion object {
        fun from(accountId: String, balance: Double): AccountDetailResponse {
            return AccountDetailResponse(accountId, balance)
        }
    }
}
