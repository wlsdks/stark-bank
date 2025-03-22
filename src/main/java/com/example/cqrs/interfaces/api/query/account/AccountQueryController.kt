package com.example.cqrs.interfaces.api.query.account

import com.example.cqrs.application.account.query.service.usecase.AccountQueryUseCase
import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/accounts/queries")
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
        @PathVariable accountId: String
    ): ResponseEntity<AccountDocument> {
        val account = accountQueryUseCase.getAccount(accountId)
        return ResponseEntity.ok(account)
    }

}