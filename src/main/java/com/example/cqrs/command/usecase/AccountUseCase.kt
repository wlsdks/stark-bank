package com.example.cqrs.command.usecase

import com.example.cqrs.command.entity.AccountEntity
import com.example.cqrs.command.entity.event.AbstractAccountEventEntity

interface AccountUseCase {
    fun getAccount(accountId: String): AccountEntity
    fun getAccountHistory(accountId: String): List<AbstractAccountEventEntity>
    fun getUserTransactions(userId: String): List<AbstractAccountEventEntity>
    fun getRelatedTransactions(correlationId: String): List<AbstractAccountEventEntity>
    fun getActiveAccountIds(): List<String>
}