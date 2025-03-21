package com.example.cqrs.command.repository

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface AccountEventStoreRepository : JpaRepository<AbstractAccountEventEntity, Long> {

    fun findByAccountIdAndEventDateAfterOrderByEventDateAsc(
        accountId: String, afterDate: LocalDateTime
    ): List<AbstractAccountEventEntity>

    fun findByAccountIdOrderByEventDateDesc(accountId: String): List<AbstractAccountEventEntity>

    fun findByAccountIdOrderByEventDateAsc(accountId: String): List<AbstractAccountEventEntity>

    fun findByMetadataCorrelationId(correlationId: String): List<AbstractAccountEventEntity>

    fun findByMetadataUserIdOrderByEventDateDesc(userId: String): List<AbstractAccountEventEntity>

    fun countByAccountIdAndEventDateAfter(accountId: String, afterDate: LocalDateTime): Long

}