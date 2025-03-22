package com.example.cqrs.interfaces.api.command.product.dto.request

// 상품 비활성화 요청
data class DeactivateProductRequest(
    val reason: String?
) {
    companion object {
        fun of(reason: String?): DeactivateProductRequest {
            return DeactivateProductRequest(reason)
        }
    }
}