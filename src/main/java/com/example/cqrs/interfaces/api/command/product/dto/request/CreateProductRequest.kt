package com.example.cqrs.interfaces.api.command.product.dto.request

import com.example.cqrs.infrastructure.persistence.command.entity.ProductType

data class CreateProductRequest(
    val productId: String,
    val name: String,
    val type: ProductType,
    val description: String?,
    val interestRate: Double,
    val termInMonths: Int,
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