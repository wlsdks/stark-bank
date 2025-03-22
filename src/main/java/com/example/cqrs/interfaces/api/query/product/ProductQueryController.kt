package com.example.cqrs.interfaces.api.query.product

import com.example.cqrs.application.product.query.service.usecase.ProductQueryUseCase
import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductSummaryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 금융 상품 조회(Query) API 컨트롤러
 * 상품 조회 관련 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/products/queries")
class ProductQueryController(
    private val productQueryUseCase: ProductQueryUseCase
) {

    /**
     * 상품 ID로 상품 상세 정보를 조회합니다.
     *
     * @param productId 조회할 상품 ID
     * @return 상품 상세 정보
     */
    @GetMapping("/{productId}")
    fun getProductById(
        @PathVariable productId: String
    ): ResponseEntity<ProductDetailResponse> {
        val product = productQueryUseCase.getProductById(productId)
        return ResponseEntity.ok(product)
    }

    /**
     * 활성화된 모든 상품 목록을 조회합니다.
     *
     * @return 활성화된 상품 목록
     */
    @GetMapping("/active")
    fun getActiveProducts(): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getActiveProducts()
        return ResponseEntity.ok(products)
    }

    /**
     * 특정 상품 유형의 활성화된 상품 목록을 조회합니다.
     *
     * @param type 상품 유형
     * @return 해당 유형의 활성화된 상품 목록
     */
    @GetMapping("/type/{type}")
    fun getProductsByType(
        @PathVariable type: ProductType
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getProductsByType(type)
        return ResponseEntity.ok(products)
    }

    /**
     * 특정 금리 범위 내의 활성화된 상품 목록을 조회합니다.
     *
     * @param minRate 최소 금리
     * @param maxRate 최대 금리
     * @return 해당 금리 범위의 활성화된 상품 목록
     */
    @GetMapping("/interest-range")
    fun getProductsByInterestRange(
        @RequestParam minRate: Double,
        @RequestParam maxRate: Double
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getProductsByInterestRange(minRate, maxRate)
        return ResponseEntity.ok(products)
    }

    /**
     * 특정 최소 가입 금액 이하의 활성화된 상품 목록을 조회합니다.
     *
     * @param maxAmount 최대 최소 가입 금액
     * @return 해당 가입 금액 조건의 활성화된 상품 목록
     */
    @GetMapping("/minimum-amount")
    fun getProductsByMinimumAmount(
        @RequestParam maxAmount: Double
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getProductsByMinimumAmount(maxAmount)
        return ResponseEntity.ok(products)
    }

    /**
     * 상품 검색을 수행합니다.
     * 상품명 또는 설명에 키워드가 포함된 상품을 검색합니다.
     *
     * @param keyword 검색 키워드
     * @return 검색 결과 상품 목록
     */
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam keyword: String
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.searchProducts(keyword)
        return ResponseEntity.ok(products)
    }

    /**
     * 추천 상품 목록을 조회합니다.
     * 시스템에서 특별히 추천하는 상품 목록을 반환합니다.
     *
     * @return 추천 상품 목록
     */
    @GetMapping("/featured")
    fun getFeaturedProducts(): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getFeaturedProducts()
        return ResponseEntity.ok(products)
    }

}