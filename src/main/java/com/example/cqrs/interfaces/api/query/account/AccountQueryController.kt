package com.example.cqrs.interfaces.api.query.account

import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument
import com.example.cqrs.application.event.retry.AccountQueryUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountQueryController(
    private val accountQueryUseCase: AccountQueryUseCase
) {

    @GetMapping("/{accountId}")
    fun getAccount(
        @PathVariable accountId: String
    ): ResponseEntity<AccountDocument> {
        val account = accountQueryUseCase.getAccount(accountId)
        return ResponseEntity.ok(account)
    }

}