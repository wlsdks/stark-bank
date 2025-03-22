package com.example.cqrs.query.controller

import com.example.cqrs.query.document.AccountDocument
import com.example.cqrs.query.usecase.AccountQueryUseCase
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