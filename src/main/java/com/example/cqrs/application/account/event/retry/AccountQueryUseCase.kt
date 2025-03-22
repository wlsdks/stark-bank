package com.example.cqrs.application.account.event.retry

import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument

interface AccountQueryUseCase {
    fun getAccount(accountId: String): AccountDocument?
}