package com.example.cqrs.infrastructure.eventstore.entity.failure

import com.example.cqrs.infrastructure.eventstore.entity.enumerate.FailureStatus
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 이벤트 처리 실패 기록 엔티티
 * 이벤트 처리 과정에서 발생한 실패를 기록하고 추적하기 위한 엔티티
 */
@Entity
@Table(name = "event_processing_failures")
class EventProcessingFailureEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val eventId: Long,

    @Column(nullable = false)
    val eventType: String,

    @Column(nullable = false)
    val entityId: String,

    @Column(nullable = false, length = 1000)
    val failureReason: String,

    @Column(nullable = false)
    val failureTime: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var retryCount: Int = 0,

    @Column(nullable = true)
    var lastRetryTime: LocalDateTime? = null,

    @Column(nullable = true)
    var resolvedAt: LocalDateTime? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: FailureStatus = FailureStatus.PENDING
) {
    /**
     * 재시도 횟수를 증가시킵니다.
     */
    fun incrementRetryCount() {
        this.retryCount++
        this.lastRetryTime = LocalDateTime.now()
    }

    /**
     * 실패 상태를 해결됨으로 표시합니다.
     */
    fun markAsResolved() {
        this.status = FailureStatus.RESOLVED
        this.resolvedAt = LocalDateTime.now()
    }

    /**
     * 실패 상태를 재시도 횟수 초과로 표시합니다.
     */
    fun markAsExhausted() {
        this.status = FailureStatus.EXHAUSTED
    }

    companion object {
        /**
         * 새 실패 기록 생성
         */
        fun of(
            eventId: Long,
            eventType: String,
            entityId: String,
            failureReason: String
        ): EventProcessingFailureEntity {
            return EventProcessingFailureEntity(
                eventId = eventId,
                eventType = eventType,
                entityId = entityId,
                failureReason = failureReason
            )
        }
    }
}
