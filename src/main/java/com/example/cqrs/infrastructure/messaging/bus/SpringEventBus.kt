package com.example.cqrs.infrastructure.messaging.bus

import com.example.cqrs.domain.event.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringEventBus(
    private val eventPublisher: ApplicationEventPublisher
) : EventBus {

    override fun publish(event: DomainEvent) {
        eventPublisher.publishEvent(event)
    }

    override fun publishAll(events: Collection<DomainEvent>) {
        events.forEach { publish(it) }
    }

}