package com.example.cqrs.command.dto

data class TransferRequest(
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Double,
    val userId: String
) {
    companion object {
        fun of(fromAccountId: String, toAccountId: String, amount: Double, userId: String): TransferRequest {
            return TransferRequest(fromAccountId, toAccountId, amount, userId)
        }
    }
}
