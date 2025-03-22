package com.example.cqrs.interfaces.api.command.product.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 수정 요청")
data class UpdateProductRequest(
    @Schema(description = "상품명", example = "예금 상품")
    val name: String,

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
            name: String,
            description: String?,
            interestRate: Double,
            termInMonths: Int,
            minimumAmount: Double
        ): UpdateProductRequest {
            return UpdateProductRequest(
                name, description, interestRate, termInMonths, minimumAmount
            )
        }
    }
}