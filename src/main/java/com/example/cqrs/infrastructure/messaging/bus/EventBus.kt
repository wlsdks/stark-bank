package com.example.cqrs.infrastructure.messaging.bus

import com.example.cqrs.domain.event.DomainEvent

interface EventBus {
    fun publish(event: DomainEvent)
    fun publishAll(events: Collection<DomainEvent>)
}
