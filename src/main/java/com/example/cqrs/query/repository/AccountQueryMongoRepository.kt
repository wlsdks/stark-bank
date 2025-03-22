package com.example.cqrs.query.repository

import com.example.cqrs.query.document.AccountDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface AccountQueryMongoRepository : MongoRepository<AccountDocument, String> {
    fun findByAccountId(accountId: String): AccountDocument?
}