package com.example.cqrs.interfaces.api.command.product

import com.example.cqrs.application.product.command.service.usecase.ProductCommandUseCase
import com.example.cqrs.interfaces.api.command.product.dto.request.CreateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.DeactivateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.request.UpdateProductRequest
import com.example.cqrs.interfaces.api.command.product.dto.response.ProductDetailResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 금융 상품 명령(Command) API 컨트롤러
 * 상품 생성, 수정, 비활성화 등의 상태 변경 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/products/commands")
@Tag(name = "상품 명령 API", description = "금융 상품 생성, 수정, 비활성화 등 상태 변경 관련 API")
class ProductCommandController(
    private val productCommandUseCase: ProductCommandUseCase
) {

    @PostMapping("/create")
    @Operation(
        summary = "금융 상품 생성",
        description = "새로운 금융 상품을 생성합니다. 상품 ID는 중복될 수 없습니다."
    )
    fun createProduct(
        @RequestBody request: CreateProductRequest,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        val productId = productCommandUseCase.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(productId)
    }

    @PutMapping("/{productId}")
    @Operation(
        summary = "금융 상품 수정",
        description = "기존 금융 상품의 정보를 수정합니다. 비활성화된 상품은 수정할 수 없습니다."
    )
    fun updateProduct(
        @Parameter(description = "수정할 상품의 ID", required = true)
        @PathVariable productId: String,
        @RequestBody request: UpdateProductRequest,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<ProductDetailResponse> {
        val updatedProduct = productCommandUseCase.updateProduct(productId, request)
        return ResponseEntity.ok(updatedProduct)
    }

    @DeleteMapping("/{productId}")
    @Operation(
        summary = "금융 상품 비활성화",
        description = "금융 상품을 비활성화합니다. 이미 비활성화된 상품에 대해서는 작업이 수행되지 않습니다."
    )
    fun deactivateProduct(
        @Parameter(description = "비활성화할 상품의 ID", required = true)
        @PathVariable productId: String,
        @RequestBody request: DeactivateProductRequest,
        @Parameter(description = "요청을 수행하는 사용자 ID", required = true)
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<String> {
        productCommandUseCase.deactivateProduct(productId, request)
        return ResponseEntity.ok("상품이 비활성화되었습니다.")
    }

}