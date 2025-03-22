package com.example.cqrs.command.entity.event.base

import com.example.cqrs.command.entity.event.enumerate.EventStatus
import com.example.cqrs.command.entity.event.base.metadata.EventMetadata
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 계좌 관련 이벤트의 기본 클래스
 */
@DiscriminatorValue("AccountEvent")
@Entity
abstract class AccountEvent(
    id: Long? = null,

    @Column(nullable = false)
    val accountId: String,

    @Column(nullable = true)
    val amount: Double?,

    eventDate: LocalDateTime,
    metadata: EventMetadata,
    status: EventStatus = EventStatus.PENDING,
    version: Long? = null
) : BaseEvent(id, eventDate, metadata, status, version)
