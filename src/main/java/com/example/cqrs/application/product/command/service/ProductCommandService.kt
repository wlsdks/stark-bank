package com.example.cqrs.application.product.command.service

import com.example.cqrs.application.product.command.service.usecase.ProductCommandUseCase
import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductCreatedEventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductDeactivatedEventEntity
import com.example.cqrs.infrastructure.eventstore.entity.event.product.ProductUpdatedEventEntity
import com.example.cqrs.infrastructure.eventstore.repository.EventStoreRepository
import com.example.cqrs.infrastructure.persistence.command.entity.ProductEntity
import com.example.cqrs.infrastructure.persistence.command.repository.ProductRepository
import com.example.cqrs.interfaces.api.command.product.dto.request.CreateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.DeactivateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.UpdateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ProductCommandService(
    private val productRepository: ProductRepository,
    private val eventStoreRepository: EventStoreRepository,
    private val eventPublisher: ApplicationEventPublisher
) : ProductCommandUseCase {

    /**
     * 새로운 금융 상품을 생성합니다.
     *
     * @param request 상품 생성 요청 DTO
     * @return 생성된 상품 ID
     * @throws IllegalArgumentException 상품이 이미 존재하는 경우
     */
    override fun createProduct(
        request: CreateProductRequest
    ): String {
        // 상품 생성 요청 유효성 검사 (중복 검사)
        if (productRepository.existsById(request.productId)) {
            throw IllegalArgumentException("이미 존재하는 상품입니다: ${request.productId}")
        }

        // 금리, 기간, 최소 금액 유효성 검사
        validateProductDetails(request.interestRate, request.termInMonths, request.minimumAmount)

        // 상품 엔티티 생성 및 저장
        val product = ProductEntity.of(
            productId = request.productId,
            name = request.name,
            type = request.type,
            description = request.description,
            interestRate = request.interestRate,
            termInMonths = request.termInMonths,
            minimumAmount = request.minimumAmount
        )
        productRepository.save(product)

        // 상품 생성 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, "SYSTEM")
        val event = ProductCreatedEventEntity.of(
            productId = request.productId,
            name = request.name,
            type = request.type,
            description = request.description,
            interestRate = request.interestRate,
            termInMonths = request.termInMonths,
            minimumAmount = request.minimumAmount,
            eventDate = LocalDateTime.now(),
            metadata = eventMetadata
        )

        // 이벤트 저장 및 발행
        eventStoreRepository.save(event)
        eventPublisher.publishEvent(event)

        return request.productId
    }

    /**
     * 기존 금융 상품을 수정합니다.
     *
     * @param productId 수정할 상품 ID
     * @param request 상품 수정 요청 DTO
     * @return 수정된 상품 상세 정보
     * @throws IllegalArgumentException 상품이 존재하지 않거나 이미 비활성화된 경우
     */
    override fun updateProduct(productId: String, request: UpdateProductRequest): ProductDetailResponse {
        // 금리, 기간, 최소 금액 유효성 검사
        validateProductDetails(request.interestRate, request.termInMonths, request.minimumAmount)

        // 상품 조회
        val product = getProductEntity(productId)

        // 비활성화된 상품인지 확인
        if (!product.active) {
            throw IllegalArgumentException("비활성화된 상품은 수정할 수 없습니다: $productId")
        }

        // 상품 정보 업데이트
        product.updateDetails(
            name = request.name,
            description = request.description,
            interestRate = request.interestRate,
            termInMonths = request.termInMonths,
            minimumAmount = request.minimumAmount
        )
        productRepository.save(product)

        // 상품 수정 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, "SYSTEM")
        val event = ProductUpdatedEventEntity.of(
            productId = productId,
            name = request.name,
            description = request.description,
            interestRate = request.interestRate,
            termInMonths = request.termInMonths,
            minimumAmount = request.minimumAmount,
            eventDate = LocalDateTime.now(),
            metadata = eventMetadata
        )

        // 이벤트 저장 및 발행
        eventStoreRepository.save(event)
        eventPublisher.publishEvent(event)

        return ProductDetailResponse.from(product)
    }

    /**
     * 금융 상품을 비활성화합니다.
     *
     * @param productId 비활성화할 상품 ID
     * @param request 비활성화 요청 DTO (사유 포함)
     * @throws IllegalArgumentException 상품이 존재하지 않거나 이미 비활성화된 경우
     */
    override fun deactivateProduct(productId: String, request: DeactivateProductRequest) {
        // 상품 조회
        val product = getProductEntity(productId)

        // 이미 비활성화된 상품인지 확인
        if (!product.active) {
            throw IllegalArgumentException("이미 비활성화된 상품입니다: $productId")
        }

        // 상품 비활성화
        product.deactivate()
        productRepository.save(product)

        // 상품 비활성화 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, "SYSTEM")
        val event = ProductDeactivatedEventEntity.of(
            productId = productId,
            reason = request.reason,
            eventDate = LocalDateTime.now(),
            metadata = eventMetadata
        )

        // 이벤트 저장 및 발행
        eventStoreRepository.save(event)
        eventPublisher.publishEvent(event)
    }

    /**
     * 상품 ID로 상품 엔티티를 조회합니다.
     *
     * @param productId 조회할 상품 ID
     * @return 상품 엔티티
     * @throws IllegalArgumentException 상품이 존재하지 않는 경우
     */
    private fun getProductEntity(productId: String): ProductEntity {
        return productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다: $productId") }
    }

    /**
     * 상품 정보의 유효성을 검사합니다.
     *
     * @param interestRate 이자율
     * @param termInMonths 기간 (월)
     * @param minimumAmount 최소 가입 금액
     * @throws IllegalArgumentException 유효하지 않은 값이 있는 경우
     */
    private fun validateProductDetails(interestRate: Double, termInMonths: Int, minimumAmount: Double) {
        if (interestRate < 0) {
            throw IllegalArgumentException("이자율은 0 이상이어야 합니다: $interestRate")
        }
        if (termInMonths <= 0) {
            throw IllegalArgumentException("가입 기간은 1개월 이상이어야 합니다: $termInMonths")
        }
        if (minimumAmount < 0) {
            throw IllegalArgumentException("최소 가입 금액은 0 이상이어야 합니다: $minimumAmount")
        }
    }

}