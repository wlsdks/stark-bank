package com.example.cqrs.interfaces.api.command.product.dto.request

import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 생성 요청")
data class CreateProductRequest(
    @Schema(description = "상품 ID", example = "P001")
    val productId: String,

    @Schema(description = "상품명", example = "예금 상품")
    val name: String,

    @Schema(description = "상품 유형", example = "DEPOSIT")
    val type: ProductType,

    @Schema(description = "상품 설명", example = "고정 이율 예금 상품")
    val description: String?,

    @Schema(description = "이자율", example = "0.02")
    val interestRate: Double,

    @Schema(description = "상품 기간(월)", example = "12")
    val termInMonths: Int,

    @Schema(description = "최소 금액", example = "1000")
    val minimumAmount: Double
) {
    companion object {
        fun of(
            productId: String,
            name: String,
            type: ProductType,
            description: String?,
            interestRate: Double,
            termInMonths: Int,
            minimumAmount: Double
        ): CreateProductRequest {
            return CreateProductRequest(productId, name, type, description, interestRate, termInMonths, minimumAmount)
        }
    }
}