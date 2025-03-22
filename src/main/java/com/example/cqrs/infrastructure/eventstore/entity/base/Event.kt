package com.example.cqrs.infrastructure.eventstore.entity.base

import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventStatus
import java.time.LocalDateTime

interface Event {
    val id: Long?
    val eventDate: LocalDateTime
    val metadata: EventMetadata
    val status: EventStatus
    val version: Long?

    fun markAsProcessed()
    fun markAsFailed()
}
