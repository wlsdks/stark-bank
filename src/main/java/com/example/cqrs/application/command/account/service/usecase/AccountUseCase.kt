package com.example.cqrs.application.command.account.service.usecase

import com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity
import com.example.cqrs.infrastructure.eventstore.base.AccountEvent

interface AccountUseCase {
    fun getAccount(accountId: String): com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity
    fun getAccountHistory(accountId: String): List<AccountEvent>
    fun getUserTransactions(userId: String): List<AccountEvent>
    fun getRelatedTransactions(correlationId: String): List<AccountEvent>
    fun getActiveAccountIds(): List<String>
}