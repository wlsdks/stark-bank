package com.example.cqrs.interfaces.api.command.account.dto.request

data class WithdrawRequest(
    val accountId: String,
    val amount: Double,
    val userId: String
) {
    companion object {
        fun of(accountId: String, amount: Double, userId: String): WithdrawRequest {
            return WithdrawRequest(accountId, amount, userId)
        }
    }
}
