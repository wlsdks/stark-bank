package com.example.cqrs.interfaces.api.command.product.dto.response

import com.example.cqrs.infrastructure.persistence.command.entity.ProductEntity
import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.infrastructure.persistence.query.document.ProductDocument
import java.time.LocalDateTime

// 상품 상세 응답
data class ProductDetailResponse(
    val productId: String,
    val name: String,
    val type: ProductType,
    val description: String?,
    val interestRate: Double,
    val termInMonths: Int,
    val minimumAmount: Double,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(product: ProductEntity): ProductDetailResponse {
            return ProductDetailResponse(
                productId = product.productId,
                name = product.name,
                type = product.type,
                description = product.description,
                interestRate = product.interestRate,
                termInMonths = product.termInMonths,
                minimumAmount = product.minimumAmount,
                active = product.active,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }

        fun from(product: ProductDocument): ProductDetailResponse {
            return ProductDetailResponse(
                productId = product.productId,
                name = product.name,
                type = ProductType.valueOf(product.type.toString()), // String을 Enum으로 변환
                description = product.description,
                interestRate = product.interestRate,
                termInMonths = product.termInMonths,
                minimumAmount = product.minimumAmount,
                active = product.active,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }
}