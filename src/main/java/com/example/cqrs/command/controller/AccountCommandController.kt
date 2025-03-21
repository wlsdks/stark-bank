package com.example.cqrs.command.controller

import com.example.cqrs.command.dto.CreateAccountRequest
import com.example.cqrs.command.dto.DepositRequest
import com.example.cqrs.command.dto.TransferRequest
import com.example.cqrs.command.usecase.AccountCommandUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/accounts")
@RestController
class AccountCommandController(
    private val accountCommandUseCase: AccountCommandUseCase
) {

    @PostMapping("/{accountId}")
    fun createAccount(
        @PathVariable("accountId") accountId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = CreateAccountRequest.of(accountId, userId)
        accountCommandUseCase.createAccount(request)
        return ResponseEntity.ok("계좌가 생성되었습니다.")
    }

    @PostMapping("/{accountId}/deposit")
    fun createDeposit(
        @PathVariable("accountId") accountId: String,
        @RequestParam("amount") amount: Double,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = DepositRequest.of(accountId, amount, userId)
        accountCommandUseCase.depositMoney(request)
        return ResponseEntity.ok("입금이 완료되었습니다.")
    }

    @PostMapping("/{accountId}/withdraw")
    fun createWithdraw(
        @PathVariable("accountId") accountId: String,
        @RequestParam("amount") amount: Double,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = DepositRequest.of(accountId, amount, userId)
        accountCommandUseCase.withdrawMoney(request)
        return ResponseEntity.ok("출금이 완료되었습니다.")
    }

    @PostMapping("/transfer")
    fun createTransfer(
        @RequestBody request: TransferRequest,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val request = TransferRequest.of(request.fromAccountId, request.toAccountId, request.amount, userId)
        accountCommandUseCase.transfer(request)
        return ResponseEntity.ok("이체가 완료되었습니다.")
    }

}