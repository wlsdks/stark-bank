package com.example.cqrs.application.common.sync

import com.example.cqrs.application.account.query.service.usecase.AccountUseCase
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.entity.event.account.AccountCreatedEventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductCreatedEventEntity
import com.example.cqrs.infrastructure.eventstore.repository.AccountEventRepository
import com.example.cqrs.infrastructure.eventstore.repository.EventStoreRepository
import com.example.cqrs.infrastructure.persistence.command.entity.ProductEntity
import com.example.cqrs.infrastructure.persistence.command.repository.AccountRepository
import com.example.cqrs.infrastructure.persistence.command.repository.ProductRepository
import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument
import com.example.cqrs.infrastructure.persistence.query.document.ProductDocument
import com.example.cqrs.infrastructure.persistence.query.repository.AccountMongoRepository
import com.example.cqrs.infrastructure.persistence.query.repository.ProductMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * 데이터 동기화 서비스
 * PostgreSQL과 MongoDB 간의 데이터 동기화를 담당
 */
@Service
class DataSynchronizationService(
    private val accountUseCase: AccountUseCase,
    private val accountRepository: AccountRepository,
    private val productRepository: ProductRepository,
    private val accountMongoRepository: AccountMongoRepository,
    private val productMongoRepository: ProductMongoRepository,
    private val eventStoreRepository: EventStoreRepository,
    private val accountEventRepository: AccountEventRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val failureRecordService: FailureRecordService,
    private val applicationContext: ApplicationContext
) {
    private val log = LoggerFactory.getLogger(DataSynchronizationService::class.java)

    private fun getSelf(): DataSynchronizationService {
        return applicationContext.getBean(DataSynchronizationService::class.java)
    }

    /**
     * 애플리케이션 시작 시 데이터 동기화 수행
     * 트랜잭션 메서드를 내부에서 호출하므로 ApplicationContext로 프록시를 획득하여 호출 (자기 자신을 호출)
     */
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        log.info("애플리케이션 시작 - 데이터 동기화 시작")
        getSelf().synchronizeAllData() // 프록시를 통한 호출
    }

    /**
     * 모든 데이터를 동기화합니다.
     * PostgreSQL -> MongoDB 방향으로 전체 데이터를 다시 구성
     */
    @Transactional
    fun synchronizeAllData() {
        log.info("전체 데이터 동기화 시작")

        try {
            synchronizeAccounts()
            synchronizeProducts()

            log.info("전체 데이터 동기화 완료")
        } catch (e: Exception) {
            log.error("전체 데이터 동기화 실패", e)
            // 중요한 시스템 오류이므로 별도 알림 로직 추가 가능
        }
    }

    /**
     * 계좌 데이터를 동기화합니다.
     */
    private fun synchronizeAccounts() {
        log.info("계좌 데이터 동기화 시작")

        // 활성 계좌 목록 조회
        val accountIds = accountUseCase.getActiveAccountIds()
        log.info("동기화할 계좌 수: {}", accountIds.size)

        // 각 계좌에 대해 동기화 수행
        var syncCount = 0
        var errorCount = 0

        accountIds.forEach { accountId ->
            try {
                synchronizeAccountData(accountId)
                syncCount++
            } catch (e: Exception) {
                log.error("계좌 동기화 실패: {}", accountId, e)
                errorCount++

                // 실패 기록
                failureRecordService.recordFailure(
                    eventId = 0, // 이벤트 ID가 없는 경우 0으로 처리
                    eventType = "AccountSynchronization",
                    failureReason = e.message ?: "Unknown error",
                    entityId = accountId
                )
            }
        }

        log.info("계좌 동기화 완료 - 성공: {}, 실패: {}", syncCount, errorCount)
    }

    /**
     * 금융 상품 데이터를 동기화합니다.
     */
    private fun synchronizeProducts() {
        log.info("상품 데이터 동기화 시작")

        // 모든 상품 조회
        val products = productRepository.findAll()
        log.info("동기화할 상품 수: {}", products.size)

        // 각 상품에 대해 동기화 수행
        var syncCount = 0
        var errorCount = 0

        products.forEach { product ->
            try {
                synchronizeProductData(product)
                syncCount++
            } catch (e: Exception) {
                log.error("상품 동기화 실패: {}", product.productId, e)
                errorCount++

                // 실패 기록
                failureRecordService.recordFailure(
                    eventId = 0, // 이벤트 ID가 없는 경우 0으로 처리
                    eventType = "ProductSynchronization",
                    failureReason = e.message ?: "Unknown error",
                    entityId = product.productId
                )
            }
        }

        log.info("상품 동기화 완료 - 성공: {}, 실패: {}", syncCount, errorCount)
    }

    /**
     * 개별 계좌 데이터를 동기화합니다.
     */
    private fun synchronizeAccountData(accountId: String) {
        // PostgreSQL에서 계좌 정보 조회
        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("계좌를 찾을 수 없습니다: $accountId") }

        // MongoDB에서 계좌 정보 조회
        val existingDocument = accountMongoRepository.findByAccountId(accountId)

        if (existingDocument != null) {
            // 기존 문서가 있는 경우 업데이트
            existingDocument.balance = account.balance
            existingDocument.lastUpdated = LocalDateTime.now()
            accountMongoRepository.save(existingDocument)
            log.debug("계좌 문서 업데이트: {}", accountId)
        } else {
            // 기존 문서가 없는 경우 신규 생성
            val newDocument = AccountDocument.of(
                accountId = account.accountId,
                balance = account.balance,
                lastUpdated = LocalDateTime.now()
            )
            accountMongoRepository.save(newDocument)

            // 계정 생성 이벤트 발행 (필요한 경우)
            val correlationId = UUID.randomUUID().toString()
            val eventMetadata = EventMetadata.of(correlationId, null, account.userId)
            val event = AccountCreatedEventEntity.of(
                accountId = account.accountId,
                amount = account.balance,
                eventDate = LocalDateTime.now(),
                metadata = eventMetadata
            )

            eventStoreRepository.save(event)
            log.debug("계좌 문서 생성: {}", accountId)
        }
    }

    /**
     * 개별 금융 상품 데이터를 동기화합니다.
     */
    private fun synchronizeProductData(product: ProductEntity) {
        // MongoDB에서 상품 정보 조회
        val existingDocument = productMongoRepository.findByProductId(product.productId)

        if (existingDocument != null) {
            // 기존 문서가 있는 경우 업데이트
            existingDocument.name = product.name
            existingDocument.type = product.type
            existingDocument.description = product.description
            existingDocument.interestRate = product.interestRate
            existingDocument.termInMonths = product.termInMonths
            existingDocument.minimumAmount = product.minimumAmount
            existingDocument.active = product.active
            existingDocument.updatedAt = LocalDateTime.now()

            productMongoRepository.save(existingDocument)
            log.debug("상품 문서 업데이트: {}", product.productId)
        } else {
            // 기존 문서가 없는 경우 신규 생성
            val newDocument = ProductDocument.of(
                productId = product.productId,
                name = product.name,
                type = product.type,
                description = product.description,
                interestRate = product.interestRate,
                termInMonths = product.termInMonths,
                minimumAmount = product.minimumAmount,
                active = product.active,
                createdAt = product.createdAt,
                updatedAt = LocalDateTime.now()
            )

            productMongoRepository.save(newDocument)

            // 상품 생성 이벤트 발행 (필요한 경우)
            val correlationId = UUID.randomUUID().toString()
            val eventMetadata = EventMetadata.of(correlationId, null, "SYSTEM")
            val event = ProductCreatedEventEntity.of(
                productId = product.productId,
                name = product.name,
                type = product.type,
                description = product.description,
                interestRate = product.interestRate,
                termInMonths = product.termInMonths,
                minimumAmount = product.minimumAmount,
                eventDate = LocalDateTime.now(),
                metadata = eventMetadata
            )

            eventStoreRepository.save(event)
            log.debug("상품 문서 생성: {}", product.productId)
        }
    }

    /**
     * 데이터 정합성을 검증하고 불일치를 해결합니다.
     * 매일 새벽 3시에 실행됩니다.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    fun validateDataConsistency() {
        log.info("데이터 정합성 검증 시작")

        try {
            validateAccountConsistency()
            validateProductConsistency()

            log.info("데이터 정합성 검증 완료")
        } catch (e: Exception) {
            log.error("데이터 정합성 검증 실패", e)
        }
    }

    /**
     * 계좌 데이터의 정합성을 검증합니다.
     */
    private fun validateAccountConsistency() {
        log.info("계좌 데이터 정합성 검증 시작")

        // PostgreSQL의 모든 계좌 조회
        val postgresAccounts = accountRepository.findAll()
        val postgresAccountIds = postgresAccounts.map { it.accountId }.toSet()

        // MongoDB의 모든 계좌 조회
        val mongoAccounts = accountMongoRepository.findAll()
        val mongoAccountIds = mongoAccounts.map { it.accountId }.toSet()

        // PostgreSQL에는 있지만 MongoDB에 없는 계좌 (누락된 계좌)
        val missingAccounts = postgresAccountIds - mongoAccountIds

        // MongoDB에는 있지만 PostgreSQL에 없는 계좌 (잘못된 계좌)
        val invalidAccounts = mongoAccountIds - postgresAccountIds

        // 양쪽 모두 있지만 잔액이 다른 계좌 (불일치 계좌)
        val inconsistentAccounts = postgresAccounts
            .filter { postgresAccount ->
                val mongoAccount = mongoAccounts.find { it.accountId == postgresAccount.accountId }
                mongoAccount != null && mongoAccount.balance != postgresAccount.balance
            }
            .map { it.accountId }

        log.info(
            "계좌 정합성 검증 결과 - 누락: {}, 잘못됨: {}, 불일치: {}",
            missingAccounts.size, invalidAccounts.size, inconsistentAccounts.size
        )

        // 누락된 계좌 동기화
        missingAccounts.forEach { accountId ->
            try {
                log.info("누락된 계좌 동기화: {}", accountId)
                synchronizeAccountData(accountId)
            } catch (e: Exception) {
                log.error("누락된 계좌 동기화 실패: {}", accountId, e)
            }
        }

        // 잘못된 계좌 삭제
        invalidAccounts.forEach { accountId ->
            try {
                log.info("잘못된 계좌 삭제: {}", accountId)
                accountMongoRepository.deleteById(accountId)
            } catch (e: Exception) {
                log.error("잘못된 계좌 삭제 실패: {}", accountId, e)
            }
        }

        // 불일치 계좌 동기화
        inconsistentAccounts.forEach { accountId ->
            try {
                log.info("불일치 계좌 동기화: {}", accountId)
                synchronizeAccountData(accountId)
            } catch (e: Exception) {
                log.error("불일치 계좌 동기화 실패: {}", accountId, e)
            }
        }
    }

    /**
     * 금융 상품 데이터의 정합성을 검증합니다.
     */
    private fun validateProductConsistency() {
        log.info("상품 데이터 정합성 검증 시작")

        // PostgreSQL의 모든 상품 조회
        val postgresProducts = productRepository.findAll()
        val postgresProductIds = postgresProducts.map { it.productId }.toSet()

        // MongoDB의 모든 상품 조회
        val mongoProducts = productMongoRepository.findAll()
        val mongoProductIds = mongoProducts.map { it.productId }.toSet()

        // PostgreSQL에는 있지만 MongoDB에 없는 상품 (누락된 상품)
        val missingProducts = postgresProductIds - mongoProductIds

        // MongoDB에는 있지만 PostgreSQL에 없는 상품 (잘못된 상품)
        val invalidProducts = mongoProductIds - postgresProductIds

        log.info(
            "상품 정합성 검증 결과 - 누락: {}, 잘못됨: {}",
            missingProducts.size, invalidProducts.size
        )

        // 누락된 상품 동기화
        missingProducts.forEach { productId ->
            try {
                val product = productRepository.findById(productId).orElseThrow {
                    IllegalArgumentException("상품을 찾을 수 없습니다: $productId")
                }

                log.info("누락된 상품 동기화: {}", productId)
                synchronizeProductData(product)
            } catch (e: Exception) {
                log.error("누락된 상품 동기화 실패: {}", productId, e)
            }
        }

        // 잘못된 상품 삭제
        invalidProducts.forEach { productId ->
            try {
                log.info("잘못된 상품 삭제: {}", productId)
                productMongoRepository.findByProductId(productId)?.let {
                    productMongoRepository.delete(it)
                }
            } catch (e: Exception) {
                log.error("잘못된 상품 삭제 실패: {}", productId, e)
            }
        }

        // 기존 상품 정보 업데이트 (모든 필드 비교는 복잡하므로 업데이트로 대체)
        (postgresProductIds intersect mongoProductIds).forEach { productId ->
            try {
                val product = productRepository.findById(productId).orElseThrow {
                    IllegalArgumentException("상품을 찾을 수 없습니다: $productId")
                }

                log.debug("기존 상품 업데이트: {}", productId)
                synchronizeProductData(product)
            } catch (e: Exception) {
                log.error("기존 상품 업데이트 실패: {}", productId, e)
            }
        }
    }

    /**
     * 특정 계좌의 이벤트를 다시 처리합니다.
     *
     * @param accountId 계좌 ID
     * @param fromDate 이벤트 시작 일시
     */
    @Transactional
    fun reprocessAccountEvents(accountId: String, fromDate: LocalDateTime) {
        log.info("계좌 이벤트 재처리 시작: {}", accountId)

        try {
            // 특정 날짜 이후의 계좌 이벤트 조회
            val events = accountEventRepository.findByAccountIdAndEventDateAfterOrderByEventDateAsc(
                accountId, fromDate
            )

            log.info("재처리할 이벤트 수: {}", events.size)

            // 계좌 기본 정보 동기화
            synchronizeAccountData(accountId)

            // 각 이벤트 재발행
            events.forEach { event ->
                try {
                    log.debug("이벤트 재발행: {} (ID: {})", event.javaClass.simpleName, event.id)
                    eventPublisher.publishEvent(event)
                } catch (e: Exception) {
                    log.error("이벤트 재발행 실패: {} (ID: {})", event.javaClass.simpleName, event.id, e)

                    // 실패 기록
                    failureRecordService.recordFailure(
                        eventId = event.id ?: 0,
                        eventType = event.javaClass.simpleName,
                        failureReason = e.message ?: "Unknown error",
                        entityId = accountId
                    )
                }
            }

            log.info("계좌 이벤트 재처리 완료: {}", accountId)
        } catch (e: Exception) {
            log.error("계좌 이벤트 재처리 실패: {}", accountId, e)
            throw e
        }
    }
}