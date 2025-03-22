package com.example.cqrs.command.usecase

import com.example.cqrs.command.entity.AccountEntity
import com.example.cqrs.command.entity.event.base.AccountEvent

interface AccountUseCase {
    fun getAccount(accountId: String): AccountEntity
    fun getAccountHistory(accountId: String): List<AccountEvent>
    fun getUserTransactions(userId: String): List<AccountEvent>
    fun getRelatedTransactions(correlationId: String): List<AccountEvent>
    fun getActiveAccountIds(): List<String>
}