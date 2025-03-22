package com.example.cqrs.application.account.command.service.usecase

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity

interface AccountQueryUseCase {
    fun getAccount(accountId: String): AccountEntity
    fun getAccountHistory(accountId: String): List<AccountEventBaseEntity>
    fun getUserTransactions(userId: String): List<AccountEventBaseEntity>
    fun getRelatedTransactions(correlationId: String): List<AccountEventBaseEntity>
    fun getActiveAccountIds(): List<String>
}