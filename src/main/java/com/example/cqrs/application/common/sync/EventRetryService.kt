package com.example.cqrs.application.common.sync

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.eventstore.entity.base.EventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductCreatedEventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductDeactivatedEventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductUpdatedEventEntity
import com.example.cqrs.infrastructure.eventstore.repository.EventStoreRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 이벤트 재시도 서비스
 * 실패한 이벤트 처리를 주기적으로 재시도하는 서비스
 */
@Service
class EventRetryService(
    private val failureRecordService: FailureRecordService,
    private val eventStoreRepository: EventStoreRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(EventRetryService::class.java)

    // 최대 재시도 횟수
    private val maxRetryCount = 5

    /**
     * 실패한 이벤트 처리를 주기적으로 재시도합니다.
     * 5분마다 실행됩니다.
     */
    @Scheduled(fixedRate = 300000) // 5분마다
    @Transactional
    fun retryFailedEvents() {
        log.info("실패한 이벤트 재시도 작업 시작")

        // 재시도 가능한 실패 기록 조회
        val failuresToRetry = failureRecordService.findRetryableFailures(maxRetryCount)

        if (failuresToRetry.isEmpty()) {
            log.info("재시도할 이벤트가 없습니다.")
            return
        }

        log.info("{} 개의 이벤트 재시도 시작", failuresToRetry.size)

        // 각 실패 기록에 대해 재시도
        failuresToRetry.forEach { failure ->
            try {
                // 이벤트 조회
                val event = eventStoreRepository.findById(failure.eventId)
                    .orElseThrow { IllegalArgumentException("이벤트를 찾을 수 없습니다: ${failure.eventId}") }

                // 이벤트 재발행
                republishEvent(event)

                // 재시도 성공 시 해결됨으로 표시
                failureRecordService.markAsResolved(failure.id!!)

                log.info("이벤트 재시도 성공: {} (ID: {})", failure.eventType, failure.eventId)
            } catch (e: Exception) {
                // 재시도 실패 시 재시도 횟수 증가
                log.error("이벤트 재시도 실패: {} (ID: {})", failure.eventType, failure.eventId, e)
                failureRecordService.incrementRetryCount(failure.id!!, maxRetryCount)
            }
        }

        log.info("이벤트 재시도 작업 완료")
    }

    /**
     * 오래된 미해결 실패 기록을 주기적으로 확인합니다.
     * 매일 자정에 실행됩니다.
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    @Transactional(readOnly = true)
    fun checkStaleFailures() {
        log.info("오래된 미해결 실패 기록 확인 시작")

        // 24시간 이상 지난 미해결 실패 기록 조회
        val staleFailures = failureRecordService.findStaleFailures(1440) // 24시간 = 1440분

        if (staleFailures.isNotEmpty()) {
            log.warn("{} 개의 오래된 미해결 실패 기록이 있습니다.", staleFailures.size)
            // 여기서 알림 발송 등의 추가 작업 가능
        } else {
            log.info("오래된 미해결 실패 기록이 없습니다.")
        }
    }

    /**
     * 이벤트 유형에 따라 적절한 이벤트 핸들러로 이벤트를 재발행합니다.
     *
     * @param event 재발행할 이벤트
     */
    private fun republishEvent(event: EventEntity) {
        when (event) {
            // 계좌 관련 이벤트
            is AccountEventBaseEntity -> {
                log.info("계좌 이벤트 재발행: {}", event.javaClass.simpleName)
                eventPublisher.publishEvent(event)
            }

            // 상품 관련 이벤트
            is ProductCreatedEventEntity -> {
                log.info("상품 생성 이벤트 재발행: {}", event.productId)
                eventPublisher.publishEvent(event)
            }

            is ProductUpdatedEventEntity -> {
                log.info("상품 수정 이벤트 재발행: {}", event.productId)
                eventPublisher.publishEvent(event)
            }

            is ProductDeactivatedEventEntity -> {
                log.info("상품 비활성화 이벤트 재발행: {}", event.productId)
                eventPublisher.publishEvent(event)
            }

            // 기타 이벤트 유형
            else -> {
                log.warn("알 수 없는 이벤트 유형: {}", event.javaClass.name)
                eventPublisher.publishEvent(event)
            }
        }
    }

}