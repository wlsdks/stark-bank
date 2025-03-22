package com.example.cqrs.interfaces.api.command.account.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "계좌 생성 요청")
data class CreateAccountRequest(
    @Schema(description = "계좌 ID", example = "A001")
    val accountId: String,

    @Schema(description = "사용자 ID", example = "U001")
    val userId: String,
) {
    companion object {
        fun of(accountId: String, userId: String): CreateAccountRequest {
            return CreateAccountRequest(accountId, userId)
        }
    }
}
