package com.example.cqrs.application.command.account.service.usecase

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEvent
import com.example.cqrs.infrastructure.eventstore.entity.base.Event
import java.time.LocalDateTime

interface AccountEventStoreUseCase {
    fun save(event: Event)
    fun saveEventStatus(event: Event)
    fun getEvents(accountId: String, after: LocalDateTime): List<AccountEvent>
    fun getAllEvents(accountId: String): List<AccountEvent>
    fun findByMetadataCorrelationId(correlationId: String): List<AccountEvent>
    fun findByMetadataUserId(userId: String): List<AccountEvent>
    fun countEventsAfterDate(accountId: String, afterDate: LocalDateTime): Long
}