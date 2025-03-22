package com.example.cqrs.interfaces.api.command.product.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 비활성화 요청")
data class DeactivateProductRequest(
    @Schema(description = "비활성화 사유", example = "상품 판매 중단")
    val reason: String?
) {
    companion object {
        fun of(reason: String?): DeactivateProductRequest {
            return DeactivateProductRequest(reason)
        }
    }
}