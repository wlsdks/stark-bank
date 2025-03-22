package com.example.cqrs.interfaces.api.command.product.dto.response

import com.example.cqrs.infrastructure.persistence.command.entity.ProductEntity
import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.infrastructure.persistence.query.document.ProductDocument

// 상품 간단 정보 응답 (목록 조회용)
data class ProductSummaryResponse(
    val productId: String,
    val name: String,
    val type: ProductType,
    val interestRate: Double,
    val termInMonths: Int,
    val minimumAmount: Double,
    val active: Boolean
) {
    companion object {
        fun from(product: ProductEntity): ProductSummaryResponse {
            return ProductSummaryResponse(
                productId = product.productId,
                name = product.name,
                type = product.type,
                interestRate = product.interestRate,
                termInMonths = product.termInMonths,
                minimumAmount = product.minimumAmount,
                active = product.active
            )
        }

        fun from(product: ProductDocument): ProductSummaryResponse {
            return ProductSummaryResponse(
                productId = product.productId,
                name = product.name,
                type = ProductType.valueOf(product.type.toString()), // String을 Enum으로 변환
                interestRate = product.interestRate,
                termInMonths = product.termInMonths,
                minimumAmount = product.minimumAmount,
                active = product.active
            )
        }
    }
}