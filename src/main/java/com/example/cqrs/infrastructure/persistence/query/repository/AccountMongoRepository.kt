package com.example.cqrs.infrastructure.persistence.query.repository

import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface AccountMongoRepository : MongoRepository<AccountDocument, String> {
    fun findByAccountId(accountId: String): AccountDocument?
}