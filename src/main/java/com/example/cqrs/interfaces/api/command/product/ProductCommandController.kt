package com.example.cqrs.interfaces.api.command.product

import com.example.cqrs.application.product.command.service.usecase.ProductCommandUseCase
import com.example.cqrs.interfaces.api.command.product.dto.request.CreateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.DeactivateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.UpdateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 금융 상품 명령(Command) API 컨트롤러
 * 상품 생성, 수정, 비활성화 등의 상태 변경 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/products/commands")
class ProductCommandController(
    private val productCommandUseCase: ProductCommandUseCase
) {

    /**
     * 새로운 금융 상품을 생성합니다.
     *
     * @param request 상품 생성 요청 DTO
     * @param userId 작업을 수행하는 사용자 ID
     * @return 생성된 상품 ID
     */
    @PostMapping("/create")
    fun createProduct(
        @RequestBody request: CreateProductRequest,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val productId = productCommandUseCase.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(productId)
    }

    /**
     * 기존 금융 상품을 수정합니다.
     *
     * @param productId 수정할 상품 ID
     * @param request 상품 수정 요청 DTO
     * @param userId 작업을 수행하는 사용자 ID
     * @return 수정된 상품 상세 정보
     */
    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: String,
        @RequestBody request: UpdateProductRequest,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<ProductDetailResponse> {
        val updatedProduct = productCommandUseCase.updateProduct(productId, request)
        return ResponseEntity.ok(updatedProduct)
    }

    /**
     * 금융 상품을 비활성화합니다.
     *
     * @param productId 비활성화할 상품 ID
     * @param request 비활성화 요청 DTO (사유 포함)
     * @param userId 작업을 수행하는 사용자 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{productId}")
    fun deactivateProduct(
        @PathVariable productId: String,
        @RequestBody request: DeactivateProductRequest,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        productCommandUseCase.deactivateProduct(productId, request)
        return ResponseEntity.ok("상품이 비활성화되었습니다.")
    }

}