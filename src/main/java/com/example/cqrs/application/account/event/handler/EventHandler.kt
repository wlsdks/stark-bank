package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.base.EventEntity

/**
 * 이벤트 핸들러 인터페이스
 * 모든 이벤트 핸들러의 기본 인터페이스
 */
interface EventHandler<T : EventEntity> {
    // 이벤트를 처리하는 메서드
    fun handle(event: T)

    // 이 핸들러가 처리할 수 있는 이벤트 타입을 반환
    fun getEventType(): Class<T>
}
