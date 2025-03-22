package com.example.cqrs.application.account.command.service.usecase

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.base.EventEntity
import java.time.LocalDateTime

interface AccountEventStoreUseCase {
    fun save(eventEntity: EventEntity)
    fun saveEventStatus(eventEntity: EventEntity)
    fun getEvents(accountId: String, after: LocalDateTime): List<AccountEventBaseEntity>
    fun getAllEvents(accountId: String): List<AccountEventBaseEntity>
    fun findByMetadataCorrelationId(correlationId: String): List<AccountEventBaseEntity>
    fun findByMetadataUserId(userId: String): List<AccountEventBaseEntity>
    fun countEventsAfterDate(accountId: String, afterDate: LocalDateTime): Long
}