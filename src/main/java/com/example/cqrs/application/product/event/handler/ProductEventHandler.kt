package com.example.cqrs.application.account.event.handler

import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductCreatedEventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductDeactivatedEventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductUpdatedEventEntity
import com.example.cqrs.infrastructure.persistence.query.document.ProductDocument
import com.example.cqrs.infrastructure.persistence.query.repository.ProductMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * 금융 상품 이벤트 핸들러
 * 금융 상품 관련 이벤트를 처리하여 읽기 모델을 업데이트
 */
@Component
class ProductEventHandler(
    private val productMongoRepository: ProductMongoRepository,
    private val retryTemplate: RetryTemplate
) {
    private val log = LoggerFactory.getLogger(ProductEventHandler::class.java)

    /**
     * 금융 상품 생성 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleProductCreated(event: ProductCreatedEventEntity) {
        log.info("상품 생성 이벤트 처리: {}", event.productId)
        retryTemplate.execute<Void, Exception> { _ ->
            val productDocument = ProductDocument.of(
                productId = event.productId,
                name = event.name,
                type = event.type,
                description = event.description,
                interestRate = event.interestRate,
                termInMonths = event.termInMonths,
                minimumAmount = event.minimumAmount,
                active = true,
                createdAt = event.eventDate,
                updatedAt = event.eventDate
            )
            productMongoRepository.save(productDocument)
            log.info("상품 생성 완료: {}", event.productId)
            null
        }
    }

    /**
     * 금융 상품 수정 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleProductUpdated(event: ProductUpdatedEventEntity) {
        log.info("상품 수정 이벤트 처리: {}", event.productId)
        retryTemplate.execute<Void, Exception> { _ ->
            val productDocument = productMongoRepository.findByProductId(event.productId)
                ?: throw IllegalStateException("상품을 찾을 수 없음: ${event.productId}")

            productDocument.name = event.name
            productDocument.description = event.description
            productDocument.interestRate = event.interestRate
            productDocument.termInMonths = event.termInMonths
            productDocument.minimumAmount = event.minimumAmount
            productDocument.updatedAt = event.eventDate

            productMongoRepository.save(productDocument)
            log.info("상품 수정 완료: {}", event.productId)
            null
        }
    }

    /**
     * 금융 상품 비활성화 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleProductDeactivated(event: ProductDeactivatedEventEntity) {
        log.info("상품 비활성화 이벤트 처리: {}", event.productId)
        retryTemplate.execute<Void, Exception> { _ ->
            val productDocument = productMongoRepository.findByProductId(event.productId)
                ?: throw IllegalStateException("상품을 찾을 수 없음: ${event.productId}")

            productDocument.active = false
            productDocument.updatedAt = event.eventDate

            // 비활성화 이유가 있는 경우 메모에 추가
            if (!event.reason.isNullOrBlank()) {
                productDocument.description = productDocument.description?.let {
                    "$it (비활성화 사유: ${event.reason})"
                } ?: "비활성화 사유: ${event.reason}"
            }

            productMongoRepository.save(productDocument)
            log.info("상품 비활성화 완료: {}", event.productId)
            null
        }
    }

    /**
     * 이벤트 처리 중 예외 발생 시 공통 예외 처리
     * (필요에 따라 사용)
     */
    private fun handleEventProcessingException(
        eventType: String,
        productId: String,
        e: Exception
    ) {
        log.error("상품 이벤트 처리 실패: {} - {}", eventType, productId, e)
        // 알림 발송 또는 재시도 큐에 등록하는 로직 추가 가능
        throw RuntimeException("상품 이벤트 처리 실패: $eventType - $productId", e)
    }

}