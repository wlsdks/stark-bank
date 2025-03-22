package com.example.cqrs.infrastructure.eventstore.entity.base

import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventStatus
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

/**
 * 금융 상품 관련 이벤트의 기본 클래스
 */
@DiscriminatorValue("ProductEvent")
@Entity
abstract class ProductEventBaseEntity(
    id: Long? = null,
    eventDate: LocalDateTime,
    metadata: EventMetadata,
    status: EventStatus = EventStatus.PENDING,
    version: Long? = null
) : BaseEventEntity(id, eventDate, metadata, status, version)