package com.example.cqrs.infrastructure.eventstore.base

import com.example.cqrs.infrastructure.eventstore.enumerate.EventStatus
import com.example.cqrs.infrastructure.eventstore.base.metadata.EventMetadata
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
