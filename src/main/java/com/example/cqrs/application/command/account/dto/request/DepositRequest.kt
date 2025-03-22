package com.example.cqrs.application.command.account.dto.request

data class DepositRequest(
    val accountId: String,
    val amount: Double,
    val userId: String
) {
    companion object {
        fun of(accountId: String, amount: Double, userId: String): DepositRequest {
            return DepositRequest(accountId, amount, userId)
        }
    }
}
