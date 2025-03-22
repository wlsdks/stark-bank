package com.example.cqrs.infrastructure.eventstore.base

import com.example.cqrs.infrastructure.eventstore.enumerate.EventStatus
import com.example.cqrs.infrastructure.eventstore.base.metadata.EventMetadata
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 금융 상품 관련 이벤트의 기본 클래스
 */
@DiscriminatorValue("ProductEvent")
@Entity
abstract class ProductEvent(
    id: Long? = null,
    eventDate: LocalDateTime,
    metadata: EventMetadata,
    status: EventStatus = EventStatus.PENDING,
    version: Long? = null
) : BaseEvent(id, eventDate, metadata, status, version)