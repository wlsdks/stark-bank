package com.example.cqrs.interfaces.api.command.account

import com.example.cqrs.application.command.account.dto.response.AccountDetailResponse
import com.example.cqrs.application.command.account.dto.response.AccountTransactionResponse
import com.example.cqrs.application.command.account.service.usecase.AccountUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/accounts")
@RestController
class AccountController(
    private val accountUseCase: AccountUseCase
) {

    @GetMapping("/{accountId}")
    fun getAccount(
        @PathVariable("accountId") accountId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<AccountDetailResponse> {
        val account = accountUseCase.getAccount(accountId)
        val response = AccountDetailResponse.from(accountId, account.balance)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{accountId}/history")
    fun getAccountHistory(
        @PathVariable("accountId") accountId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountUseCase.getAccountHistory(accountId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}/transactions")
    fun getUserTransactions(
        @PathVariable("userId") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountUseCase.getUserTransactions(userId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/transactions/{correlationId}")
    fun getRelatedTransactions(
        @PathVariable("correlationId") correlationId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountUseCase.getRelatedTransactions(correlationId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

}