package com.example.cqrs.interfaces.api.query.account

import com.example.cqrs.application.account.command.dto.response.AccountDetailResponse
import com.example.cqrs.application.account.command.dto.response.AccountTransactionResponse
import com.example.cqrs.application.account.command.service.usecase.AccountQueryUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/accounts")
@RestController
class AccountQueryController(
    private val accountQueryUseCase: AccountQueryUseCase
) {

    @GetMapping("/{accountId}")
    fun getAccount(
        @PathVariable("accountId") accountId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<AccountDetailResponse> {
        val account = accountQueryUseCase.getAccount(accountId)
        val response = AccountDetailResponse.from(accountId, account.balance)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{accountId}/history")
    fun getAccountHistory(
        @PathVariable("accountId") accountId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountQueryUseCase.getAccountHistory(accountId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}/transactions")
    fun getUserTransactions(
        @PathVariable("userId") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountQueryUseCase.getUserTransactions(userId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/transactions/{correlationId}")
    fun getRelatedTransactions(
        @PathVariable("correlationId") correlationId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountQueryUseCase.getRelatedTransactions(correlationId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

}