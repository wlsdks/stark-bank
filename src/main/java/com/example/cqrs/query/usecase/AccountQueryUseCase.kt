package com.example.cqrs.query.usecase

import com.example.cqrs.query.document.AccountDocument

interface AccountQueryUseCase {
    fun getAccount(accountId: String): AccountDocument?
}