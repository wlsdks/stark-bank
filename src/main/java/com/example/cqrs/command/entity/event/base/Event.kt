package com.example.cqrs.command.entity.event.base

import com.example.cqrs.command.entity.event.enumerate.EventStatus
import com.example.cqrs.command.entity.event.metadata.EventMetadata
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
