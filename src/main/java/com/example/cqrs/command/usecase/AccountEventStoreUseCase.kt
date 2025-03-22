package com.example.cqrs.command.usecase

import com.example.cqrs.command.entity.event.base.AccountEvent
import com.example.cqrs.command.entity.event.base.Event
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