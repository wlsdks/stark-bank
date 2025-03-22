package com.example.cqrs.infrastructure.eventstore.repository

import com.example.cqrs.infrastructure.eventstore.entity.base.ProductEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * 금융 상품 이벤트 리포지토리
 * 금융 상품 관련 이벤트 전용 조회 기능 제공
 */
interface ProductEventRepository : JpaRepository<ProductEvent, Long> {

    /**
     * 특정 상품 ID로 모든 이벤트 조회
     */
    @Query("SELECT e FROM ProductCreatedEvent e WHERE e.productId = :productId")
    fun findByProductId(@Param("productId") productId: String): List<ProductEvent>

    /**
     * 특정 상품 유형의 모든 상품 생성 이벤트 조회
     */
    @Query("SELECT e FROM ProductCreatedEvent e WHERE e.type = :type")
    fun findProductsByType(@Param("type") type: String): List<ProductEvent>

}