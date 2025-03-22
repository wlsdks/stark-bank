package com.example.cqrs.application.account.query.service.usecase

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity

interface AccountUseCase {
    fun getAccount(accountId: String): AccountEntity
    fun getAccountHistory(accountId: String): List<AccountEventBaseEntity>
    fun getUserTransactions(userId: String): List<AccountEventBaseEntity>
    fun getRelatedTransactions(correlationId: String): List<AccountEventBaseEntity>
    fun getActiveAccountIds(): List<String>
}