package com.example.cqrs.interfaces.api.command.account

import com.example.cqrs.application.account.command.service.usecase.AccountCommandUseCase
import com.example.cqrs.interfaces.api.command.account.dto.request.CreateAccountRequest
import com.example.cqrs.interfaces.api.command.account.dto.request.DepositRequest
import com.example.cqrs.interfaces.api.command.account.dto.request.TransferRequest
import com.example.cqrs.interfaces.api.command.account.dto.request.WithdrawRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/accounts")
@RestController
@Tag(name = "계좌 명령 API", description = "계좌 생성, 입금, 출금, 이체 등 계좌 상태 변경 관련 API")
class AccountCommandController(
    private val accountCommandUseCase: AccountCommandUseCase
) {

    @PostMapping("/create/{accountId}")
    @Operation(
        summary = "계좌 생성",
        description = "새로운 계좌를 생성합니다. 계좌 ID는 중복될 수 없습니다."
    )
    fun createAccount(
        @Parameter(description = "생성할 계좌의 ID", required = true)
        @PathVariable("accountId") accountId: String,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = CreateAccountRequest.of(accountId, userId)
        accountCommandUseCase.createAccount(request)
        return ResponseEntity.ok("계좌가 생성되었습니다.")
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(
        summary = "계좌 입금",
        description = "지정된 계좌에 금액을 입금합니다. 입금 금액은 0보다 커야 합니다."
    )
    fun createDeposit(
        @Parameter(description = "입금할 계좌의 ID", required = true)
        @PathVariable("accountId") accountId: String,
        @Parameter(description = "입금할 금액", required = true)
        @RequestParam("amount") amount: Double,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = DepositRequest.of(accountId, amount, userId)
        accountCommandUseCase.depositMoney(request)
        return ResponseEntity.ok("입금이 완료되었습니다.")
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(
        summary = "계좌 출금",
        description = "지정된 계좌에서 금액을 출금합니다. 출금 금액은 0보다 크고 계좌 잔액 이하여야 합니다."
    )
    fun createWithdraw(
        @Parameter(description = "출금할 계좌의 ID", required = true)
        @PathVariable("accountId") accountId: String,
        @Parameter(description = "출금할 금액", required = true)
        @RequestParam("amount") amount: Double,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = WithdrawRequest.of(accountId, amount, userId)
        accountCommandUseCase.withdrawMoney(request)
        return ResponseEntity.ok("출금이 완료되었습니다.")
    }

    @PostMapping("/transfer")
    @Operation(
        summary = "계좌 이체",
        description = "한 계좌에서 다른 계좌로 금액을 이체합니다. 이체 금액은 0보다 크고 출금 계좌의 잔액 이하여야 합니다."
    )
    fun createTransfer(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "이체 요청 정보",
            required = true,
            content = [Content(schema = Schema(implementation = TransferRequest::class))]
        )
        @RequestBody request: TransferRequest,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = TransferRequest.of(request.fromAccountId, request.toAccountId, request.amount, userId)
        accountCommandUseCase.transfer(request)
        return ResponseEntity.ok("이체가 완료되었습니다.")
    }

}