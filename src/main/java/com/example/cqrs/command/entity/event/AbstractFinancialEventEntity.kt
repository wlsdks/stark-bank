package com.example.cqrs.command.entity.event.event

import com.example.cqrs.command.entity.event.enumerate.EventStatus
import com.example.cqrs.command.entity.event.metadata.EventMetadata
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
@Table(name = "financial_event_store")
abstract class AbstractFinancialEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val eventDate: LocalDateTime, // 이벤트 발생 일시

    @Embedded
    val metadata: EventMetadata, // 이벤트 메타데이터

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: EventStatus = EventStatus.PENDING, // 이벤트 처리 상태

    @Version
    var version: Long? = null // JPA 낙관적 잠금용 버전
) {
    fun markAsProcessed() {
        this.status = EventStatus.PROCESSED
    }

    fun markAsFailed() {
        this.status = EventStatus.FAILED
    }
}