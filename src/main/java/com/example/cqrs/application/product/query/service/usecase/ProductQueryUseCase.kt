package com.example.cqrs.application.product.query.service.usecase

import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductSummaryResponse

/**
 * 금융 상품 조회(Query) 관련 유스케이스 인터페이스
 */
interface ProductQueryUseCase {

    fun getProductById(productId: String): ProductDetailResponse
    fun getActiveProducts(): List<ProductSummaryResponse>
    fun getProductsByType(type: ProductType): List<ProductSummaryResponse>
    fun getProductsByInterestRange(minRate: Double, maxRate: Double): List<ProductSummaryResponse>
    fun getProductsByMinimumAmount(maxAmount: Double): List<ProductSummaryResponse>
    fun searchProducts(keyword: String): List<ProductSummaryResponse>
    fun getFeaturedProducts(): List<ProductSummaryResponse>

}