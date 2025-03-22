package com.example.cqrs.application.product.command.service.usecase

import com.example.cqrs.interfaces.api.command.product.dto.request.CreateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.DeactivateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.UpdateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse

/**
 * 금융 상품 명령(Command) 관련 유스케이스 인터페이스
 */
interface ProductCommandUseCase {

    /**
     * 새로운 금융 상품을 생성합니다.
     *
     * @param request 상품 생성 요청 DTO
     * @return 생성된 상품 ID
     */
    fun createProduct(request: CreateProductRequest): String

    /**
     * 기존 금융 상품을 수정합니다.
     *
     * @param productId 수정할 상품 ID
     * @param request 상품 수정 요청 DTO
     * @return 수정된 상품 상세 정보
     */
    fun updateProduct(productId: String, request: UpdateProductRequest): ProductDetailResponse

    /**
     * 금융 상품을 비활성화합니다.
     *
     * @param productId 비활성화할 상품 ID
     * @param request 비활성화 요청 DTO (사유 포함)
     */
    fun deactivateProduct(productId: String, request: DeactivateProductRequest)

}