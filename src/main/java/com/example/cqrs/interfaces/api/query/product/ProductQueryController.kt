package com.example.cqrs.interfaces.api.query.product

import com.example.cqrs.application.product.query.service.usecase.ProductQueryUseCase
import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductSummaryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 금융 상품 조회(Query) API 컨트롤러
 * 상품 조회 관련 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/products/queries")
@Tag(name = "상품 조회 API", description = "금융 상품 조회 관련 API")
class ProductQueryController(
    private val productQueryUseCase: ProductQueryUseCase
) {

    @GetMapping("/{productId}")
    @Operation(
        summary = "상품 상세 조회",
        description = "상품 ID로 금융 상품의 상세 정보를 조회합니다."
    )
    fun getProductById(
        @Parameter(description = "조회할 상품의 ID", required = true)
        @PathVariable productId: String
    ): ResponseEntity<ProductDetailResponse> {
        val product = productQueryUseCase.getProductById(productId)
        return ResponseEntity.ok(product)
    }

    @GetMapping("/active")
    @Operation(
        summary = "활성 상품 목록 조회",
        description = "현재 활성화된 모든 금융 상품 목록을 조회합니다."
    )
    fun getActiveProducts(): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getActiveProducts()
        return ResponseEntity.ok(products)
    }

    @GetMapping("/type/{type}")
    @Operation(
        summary = "상품 유형별 조회",
        description = "특정 상품 유형(예: 예금, 적금, 펀드 등)의 활성화된 금융 상품 목록을 조회합니다."
    )
    fun getProductsByType(
        @Parameter(description = "조회할 상품의 유형", required = true, schema = Schema(implementation = ProductType::class))
        @PathVariable type: ProductType
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getProductsByType(type)
        return ResponseEntity.ok(products)
    }

    @GetMapping("/interest-range")
    @Operation(
        summary = "금리 범위별 상품 조회",
        description = "특정 금리 범위(최소 금리 ~ 최대 금리) 내의 활성화된 금융 상품 목록을 조회합니다."
    )
    fun getProductsByInterestRange(
        @Parameter(description = "최소 금리(%)", required = true)
        @RequestParam minRate: Double,
        @Parameter(description = "최대 금리(%)", required = true)
        @RequestParam maxRate: Double
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getProductsByInterestRange(minRate, maxRate)
        return ResponseEntity.ok(products)
    }

    @GetMapping("/minimum-amount")
    @Operation(
        summary = "최소 가입 금액별 상품 조회",
        description = "특정 금액 이하의 최소 가입 금액을 가진 활성화된 금융 상품 목록을 조회합니다."
    )
    fun getProductsByMinimumAmount(
        @Parameter(description = "최대 최소 가입 금액", required = true)
        @RequestParam maxAmount: Double
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getProductsByMinimumAmount(maxAmount)
        return ResponseEntity.ok(products)
    }

    @GetMapping("/search")
    @Operation(
        summary = "상품 검색",
        description = "상품명 또는 설명에 특정 키워드가 포함된 활성화된 금융 상품을 검색합니다."
    )
    fun searchProducts(
        @Parameter(description = "검색 키워드", required = true)
        @RequestParam keyword: String
    ): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.searchProducts(keyword)
        return ResponseEntity.ok(products)
    }

    @GetMapping("/featured")
    @Operation(
        summary = "추천 상품 조회",
        description = "시스템이 추천하는 특별 금융 상품 목록을 조회합니다. 추천 상품은 활성화된 상품 중에서 선별됩니다."
    )
    fun getFeaturedProducts(): ResponseEntity<List<ProductSummaryResponse>> {
        val products = productQueryUseCase.getFeaturedProducts()
        return ResponseEntity.ok(products)
    }

}