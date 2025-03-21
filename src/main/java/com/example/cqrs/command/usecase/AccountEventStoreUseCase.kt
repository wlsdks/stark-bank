package com.example.cqrs.command.usecase

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity
import java.time.LocalDateTime

interface AccountEventStoreUseCase {
    fun save(event: AbstractAccountEventEntity)
    fun saveEventStatus(event: AbstractAccountEventEntity)
    fun getEvents(accountId: String, after: LocalDateTime): List<AbstractAccountEventEntity>
    fun getAllEvents(accountId: String): List<AbstractAccountEventEntity>
    fun findByMetadataCorrelationId(correlationId: String): List<AbstractAccountEventEntity>
    fun findByMetadataUserId(userId: String): List<AbstractAccountEventEntity>
    fun countEventsAfterDate(accountId: String, afterDate: LocalDateTime): Long
}