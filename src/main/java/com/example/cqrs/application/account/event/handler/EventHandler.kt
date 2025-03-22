package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.base.EventEntity

/**
 * 이벤트 핸들러 인터페이스
 * 모든 이벤트 핸들러의 기본 인터페이스
 */
interface EventHandler<T : EventEntity> {
    fun handle(event: T)
}
