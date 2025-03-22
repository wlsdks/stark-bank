package com.example.cqrs.interfaces.api.command.product.dto.request

// 상품 수정 요청
data class UpdateProductRequest(
    val name: String,
    val description: String?,
    val interestRate: Double,
    val termInMonths: Int,
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