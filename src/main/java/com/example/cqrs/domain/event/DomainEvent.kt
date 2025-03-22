package com.example.cqrs.domain.event

import java.time.LocalDateTime

/**
 * 도메인 이벤트 인터페이스
 * 모든 도메인 이벤트는 이 인터페이스를 구현해야 함
 */
interface DomainEvent {

    // 이벤트 고유 식별자
    val eventId: String

    // 이벤트 발생 시간
    val occurredOn: LocalDateTime

    // 이벤트 타입 (선택적으로 사용 가능)
    val eventType: String
        get() = this.javaClass.simpleName

    // 이벤트에 메타데이터 추가
    fun withMetadata(
        userId: String,
        correlationId: String? = null,
        causationId: String? = null
    ): DomainEvent

}