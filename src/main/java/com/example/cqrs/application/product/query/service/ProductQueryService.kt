package com.example.cqrs.application.product.query.service

import com.example.cqrs.application.product.query.service.usecase.ProductQueryUseCase
import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.infrastructure.persistence.query.repository.ProductQueryMongoRepository
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductSummaryResponse
import org.springframework.stereotype.Service

@Service
class ProductQueryService(
    private val productQueryMongoRepository: ProductQueryMongoRepository
) : ProductQueryUseCase {

    /**
     * 상품 ID로 상품 정보를 조회합니다.
     *
     * @param productId 상품 ID
     * @return 상품 상세 응답
     */
    override fun getProductById(
        productId: String
    ): ProductDetailResponse {
        val product = productQueryMongoRepository.findByProductId(productId)
            ?: throw IllegalArgumentException("상품을 찾을 수 없습니다: $productId")

        return ProductDetailResponse.from(product)
    }

    /**
     * 활성화된 상품 목록을 조회합니다.
     *
     * @return 상품 간단 정보 응답 목록
     */
    override fun getActiveProducts(): List<ProductSummaryResponse> {
        return productQueryMongoRepository.findByActiveTrue()
            .map { ProductSummaryResponse.from(it) }
    }

    /**
     * 상품 타입으로 상품 목록을 조회합니다.
     *
     * @param type 상품 타입
     * @return 상품 간단 정보 응답 목록
     */
    override fun getProductsByType(
        type: ProductType
    ): List<ProductSummaryResponse> {
        return productQueryMongoRepository.findByTypeAndActiveTrue(type)
            .map { ProductSummaryResponse.from(it) }
    }

    /**
     * 이자율 범위로 상품 목록을 조회합니다.
     *
     * @param minRate 최소 이자율
     * @param maxRate 최대 이자율
     * @return 상품 간단 정보 응답 목록
     */
    override fun getProductsByInterestRange(
        minRate: Double,
        maxRate: Double
    ): List<ProductSummaryResponse> {
        return productQueryMongoRepository.findByInterestRateBetweenAndActiveTrue(minRate, maxRate)
            .map { ProductSummaryResponse.from(it) }
    }

    /**
     * 최소 가입 금액 이하의 상품 목록을 조회합니다.
     *
     * @param maxAmount 최대 가입 금액
     * @return 상품 간단 정보 응답 목록
     */
    override fun getProductsByMinimumAmount(
        maxAmount: Double
    ): List<ProductSummaryResponse> {
        return productQueryMongoRepository.findByMinimumAmountLessThanEqualAndActiveTrue(maxAmount)
            .map { ProductSummaryResponse.from(it) }
    }

    /**
     * 상품명 또는 설명에 키워드가 포함된 상품을 검색합니다.
     *
     * @param keyword 검색 키워드
     * @return 검색 결과 상품 목록
     */
    override fun searchProducts(
        keyword: String
    ): List<ProductSummaryResponse> {
        return productQueryMongoRepository.searchProducts(keyword)
            .map { ProductSummaryResponse.from(it) }
    }

    /**
     * 추천 상품 목록을 조회합니다.
     * featuredRank 값이 있는 활성화된 상품을 순위 기준으로 정렬하여 반환합니다.
     *
     * @return 추천 상품 목록
     */
    override fun getFeaturedProducts(): List<ProductSummaryResponse> {
        return productQueryMongoRepository.findFeaturedProductsOrderByRank()
            .map { ProductSummaryResponse.from(it) }
    }

}