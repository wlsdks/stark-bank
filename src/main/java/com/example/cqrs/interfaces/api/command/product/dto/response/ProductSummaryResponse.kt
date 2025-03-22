package com.example.cqrs.interfaces.api.command.product.dto.response

import com.example.cqrs.infrastructure.persistence.command.entity.ProductEntity
import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import com.example.cqrs.infrastructure.persistence.query.document.ProductDocument
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 간단 응답")
data class ProductSummaryResponse(
    @Schema(description = "상품 ID", example = "P001")
    val productId: String,

    @Schema(description = "상품명", example = "예금 상품")
    val name: String,

    @Schema(description = "상품 유형", example = "DEPOSIT")
    val type: ProductType,

    @Schema(description = "이자율", example = "0.02")
    val interestRate: Double,

    @Schema(description = "상품 기간(월)", example = "12")
    val termInMonths: Int,

    @Schema(description = "최소 금액", example = "1000")
    val minimumAmount: Double,

    @Schema(description = "활성 여부", example = "true")
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