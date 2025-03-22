package com.example.cqrs.application.common.sync

import com.example.cqrs.infrastructure.eventstore.entity.enumerate.FailureStatus
import com.example.cqrs.infrastructure.eventstore.entity.failure.EventProcessingFailureEntity
import com.example.cqrs.infrastructure.eventstore.repository.EventProcessingFailureRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class FailureRecordService(
    private val failureRepository: EventProcessingFailureRepository
) {
    private val log = LoggerFactory.getLogger(FailureRecordService::class.java)

    /**
     * 이벤트 처리 실패를 기록합니다.
     *
     * @param eventId 이벤트 ID
     * @param eventType 이벤트 타입
     * @param failureReason 실패 사유
     * @param entityId 이벤트와 관련된 엔티티 ID
     * @return 생성된 실패 기록 엔티티
     */
    @Transactional
    fun recordFailure(
        eventId: Long,
        eventType: String,
        failureReason: String,
        entityId: String
    ): EventProcessingFailureEntity {
        log.info("이벤트 처리 실패 기록: {} ({})", eventType, entityId)

        val failure = EventProcessingFailureEntity.of(
            eventId = eventId,
            eventType = eventType,
            entityId = entityId,
            failureReason = failureReason
        )

        return failureRepository.save(failure)
    }

    /**
     * 재시도 가능한 실패 기록 목록을 조회합니다.
     *
     * @param maxRetryCount 최대 재시도 횟수
     * @return 재시도 가능한 실패 기록 목록
     */
    @Transactional(readOnly = true)
    fun findRetryableFailures(
        maxRetryCount: Int
    ): List<EventProcessingFailureEntity> {
        return failureRepository.findByStatusAndRetryCountLessThan(FailureStatus.PENDING, maxRetryCount)
    }

    /**
     * 특정 시간 이상 지난 미해결 실패 기록을 조회합니다.
     *
     * @param minutesAgo 몇 분 전 기준
     * @return 오래된 미해결 실패 기록 목록
     */
    @Transactional(readOnly = true)
    fun findStaleFailures(
        minutesAgo: Long
    ): List<EventProcessingFailureEntity> {
        val beforeTime = LocalDateTime.now().minusMinutes(minutesAgo)
        return failureRepository.findByStatusInAndLastRetryTimeBefore(
            listOf(FailureStatus.PENDING, FailureStatus.RETRYING),
            beforeTime
        )
    }

    /**
     * 재시도 횟수를 증가시키고 상태를 업데이트합니다.
     *
     * @param failureId 실패 기록 ID
     * @param maxRetryCount 최대 재시도 횟수
     * @return 업데이트된 실패 기록 엔티티
     */
    @Transactional
    fun incrementRetryCount(
        failureId: Long,
        maxRetryCount: Int
    ): EventProcessingFailureEntity {
        val failure = failureRepository.findById(failureId)
            .orElseThrow { IllegalArgumentException("실패 기록을 찾을 수 없습니다: $failureId") }

        failure.incrementRetryCount()
        failure.status = FailureStatus.RETRYING

        // 최대 재시도 횟수 초과 시 처리
        if (failure.retryCount >= maxRetryCount) {
            failure.markAsExhausted()
            log.warn("최대 재시도 횟수 초과: {} (이벤트 ID: {})", failure.eventType, failure.eventId)
        }

        return failureRepository.save(failure)
    }

    /**
     * 실패 기록을 해결됨으로 표시합니다.
     *
     * @param failureId 실패 기록 ID
     * @return 업데이트된 실패 기록 엔티티
     */
    @Transactional
    fun markAsResolved(
        failureId: Long
    ): EventProcessingFailureEntity {
        val failure = failureRepository.findById(failureId)
            .orElseThrow { IllegalArgumentException("실패 기록을 찾을 수 없습니다: $failureId") }

        failure.markAsResolved()
        log.info("실패 기록 해결됨: {} (이벤트 ID: {})", failure.eventType, failure.eventId)

        return failureRepository.save(failure)
    }

    /**
     * 특정 엔티티의 모든 실패 기록을 조회합니다.
     *
     * @param entityId 엔티티 ID
     * @return 해당 엔티티 관련 실패 기록 목록
     */
    @Transactional(readOnly = true)
    fun findFailuresByEntityId(
        entityId: String
    ): List<EventProcessingFailureEntity> {
        return failureRepository.findByEntityId(entityId)
    }

}