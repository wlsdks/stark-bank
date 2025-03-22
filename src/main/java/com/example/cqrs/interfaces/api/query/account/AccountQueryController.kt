package com.example.cqrs.interfaces.api.query.account

import com.example.cqrs.application.account.command.service.usecase.AccountQueryUseCase
import com.example.cqrs.interfaces.api.command.account.dto.response.AccountDetailResponse
import com.example.cqrs.interfaces.api.command.account.dto.response.AccountTransactionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/accounts/queries")
@RestController
@Tag(name = "계좌 쿼리 API", description = "읽기 모델(MongoDB)에서 계좌 정보를 조회하는 API")
class AccountQueryController(
    private val accountQueryUseCase: AccountQueryUseCase
) {

    @GetMapping("/{accountId}")
    @Operation(
        summary = "계좌 정보 조회 (읽기 모델)",
        description = "MongoDB에 저장된 계좌 정보를 조회합니다. 이는 읽기 최적화된 모델에서의 조회입니다."
    )
    fun getAccount(
        @Parameter(description = "조회할 계좌의 ID", required = true)
        @PathVariable("accountId") accountId: String,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<AccountDetailResponse> {
        val account = accountQueryUseCase.getAccount(accountId)
        val response = AccountDetailResponse.from(accountId, account.balance)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{accountId}/history")
    @Operation(
        summary = "계좌 거래 내역 조회",
        description = "특정 계좌의 모든 거래 내역을 조회합니다. 계좌 생성부터 현재까지의 모든 이벤트가 시간순으로 반환됩니다."
    )
    fun getAccountHistory(
        @Parameter(description = "조회할 계좌의 ID", required = true)
        @PathVariable("accountId") accountId: String,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountQueryUseCase.getAccountHistory(accountId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}/transactions")
    @Operation(
        summary = "사용자 거래 내역 조회",
        description = "특정 사용자가 수행한 모든 계좌 거래 내역을 조회합니다. 모든 계좌에 대한 거래가 포함되며, 최신 거래 순으로 반환됩니다."
    )
    fun getUserTransactions(
        @Parameter(description = "조회할 사용자의 ID", required = true)
        @PathVariable("userId") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountQueryUseCase.getUserTransactions(userId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/transactions/{correlationId}")
    @Operation(
        summary = "연관 거래 내역 조회",
        description = "특정 상관 ID로 연결된 모든 거래 내역을 조회합니다. 예를 들어, 계좌 이체 시 출금과 입금 거래가 함께 조회됩니다."
    )
    fun getRelatedTransactions(
        @Parameter(description = "조회할 거래 상관 ID", required = true)
        @PathVariable("correlationId") correlationId: String,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<List<AccountTransactionResponse>> {
        val response: List<AccountTransactionResponse> = accountQueryUseCase.getRelatedTransactions(correlationId)
            .map { event -> AccountTransactionResponse.from(event) }

        return ResponseEntity.ok(response)
    }

}