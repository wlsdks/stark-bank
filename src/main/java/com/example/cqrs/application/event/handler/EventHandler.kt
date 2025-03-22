package com.example.cqrs.application.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.base.Event

/**
 * 이벤트 핸들러 인터페이스
 * 모든 이벤트 핸들러의 기본 인터페이스
 */
interface EventHandler<T : Event> {
    fun handle(event: T)
}
