package com.example.cqrs.application.command.account.dto.request

data class CreateAccountRequest(
    val accountId: String,
    val userId: String,
) {
    companion object {
        fun of(accountId: String, userId: String): CreateAccountRequest {
            return CreateAccountRequest(accountId, userId)
        }
    }
}
