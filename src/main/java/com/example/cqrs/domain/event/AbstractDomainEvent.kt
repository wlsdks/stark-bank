package com.example.cqrs.domain.event

import java.time.LocalDateTime
import java.util.*

/**
 * 도메인 이벤트의 기본 구현을 제공하는 추상 클래스
 */
abstract class AbstractDomainEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    open val metadata: EventMetadata = EventMetadata()
) : DomainEvent {

    /**
     * 이벤트 메타데이터
     */
    data class EventMetadata(
        val correlationId: String? = null,
        val causationId: String? = null,
        val userId: String? = null,
        val version: String = "1.0"
    )
}