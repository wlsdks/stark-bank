package com.example.cqrs.infrastructure.eventstore.entity.base

import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventStatus
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 모든 이벤트의 기본 구현을 제공하는 추상 클래스
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
@Table(name = "event_store")
abstract class BaseEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null,

    @Column(nullable = false)
    override val eventDate: LocalDateTime,

    @Embedded
    override val metadata: EventMetadata,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    override var status: EventStatus = EventStatus.PENDING,

    @Version
    override var version: Long? = null
) : Event {
    override fun markAsProcessed() {
        this.status = EventStatus.PROCESSED
    }

    override fun markAsFailed() {
        this.status = EventStatus.FAILED
    }
}