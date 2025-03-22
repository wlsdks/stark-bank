package com.example.cqrs.infrastructure.eventstore.repository

import com.example.cqrs.infrastructure.eventstore.entity.enumerate.FailureStatus
import com.example.cqrs.infrastructure.eventstore.entity.failure.EventProcessingFailureEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

/**
 * 이벤트 처리 실패 리포지토리
 */
interface EventProcessingFailureRepository : JpaRepository<EventProcessingFailureEntity, Long> {
    // 특정 상태의 실패 기록 조회
    fun findByStatus(status: FailureStatus): List<EventProcessingFailureEntity>

    // 특정 상태이면서 최대 재시도 횟수보다 적게 시도된 실패 기록 조회
    fun findByStatusAndRetryCountLessThan(status: FailureStatus, maxRetryCount: Int): List<EventProcessingFailureEntity>

    // 특정 이벤트 ID의 실패 기록 조회
    fun findByEventId(eventId: Long): List<EventProcessingFailureEntity>

    // 특정 엔티티 ID의 실패 기록 조회
    fun findByEntityId(entityId: String): List<EventProcessingFailureEntity>

    // 특정 기간 내의 실패 기록 조회
    fun findByFailureTimeBetween(start: LocalDateTime, end: LocalDateTime): List<EventProcessingFailureEntity>

    // 미해결이고 마지막 재시도 시간이 특정 시간 이전인 기록 조회
    fun findByStatusInAndLastRetryTimeBefore(
        statuses: List<FailureStatus>,
        beforeTime: LocalDateTime
    ): List<EventProcessingFailureEntity>
}