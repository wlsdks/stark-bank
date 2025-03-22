package com.example.cqrs.infrastructure.persistence.query.repository

import com.example.cqrs.infrastructure.persistence.query.document.ProductDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

/**
 * 금융 상품 쿼리 리포지토리
 * MongoDB에 저장된 금융 상품 읽기 모델에 대한 조회 작업 지원
 */
@Repository
interface ProductQueryMongoRepository : MongoRepository<ProductDocument, String> {

    /**
     * 상품 ID로 상품 조회
     */
    fun findByProductId(productId: String): ProductDocument?

    /**
     * 상품 유형으로 상품 목록 조회
     */
    fun findByType(type: String): List<ProductDocument>

    /**
     * 활성화된 상품만 조회
     */
    fun findByActiveTrue(): List<ProductDocument>

    /**
     * 상품 유형과 활성화 상태로 조회
     */
    fun findByTypeAndActiveTrue(type: String): List<ProductDocument>

    /**
     * 최소 가입 금액 이하의 상품 조회
     */
    fun findByMinimumAmountLessThanEqualAndActiveTrue(maxAmount: Double): List<ProductDocument>

    /**
     * 이자율 범위로 상품 조회
     */
    fun findByInterestRateBetweenAndActiveTrue(minRate: Double, maxRate: Double): List<ProductDocument>

    /**
     * 태그로 상품 검색
     */
    fun findByTagsContainingAndActiveTrue(tag: String): List<ProductDocument>

    /**
     * 추천 상품 조회 (순위 오름차순)
     */
    @Query("{ 'featuredRank': { \$ne: null }, 'active': true }")
    fun findFeaturedProductsOrderByRank(): List<ProductDocument>

    /**
     * 상품명 또는 설명에 키워드가 포함된 상품 검색
     */
    @Query("{ '\$or': [ { 'name': { \$regex: ?0, \$options: 'i' } }, { 'description': { \$regex: ?0, \$options: 'i' } } ], 'active': true }")
    fun searchProducts(keyword: String): List<ProductDocument>

}