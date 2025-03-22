package com.example.cqrs.infrastructure.persistence.query.document

import com.example.cqrs.infrastructure.persistence.command.entity.ProductType
import jakarta.persistence.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * 금융 상품 문서 (MongoDB)
 * 읽기 모델용 금융 상품 정보를 저장
 */
@Document(collection = "product")
class ProductDocument(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val productId: String,    // 상품 ID

    var name: String,         // 상품명

    var type: ProductType,    // 상품 유형 (예금, 적금, 펀드 등)

    var description: String?, // 상품 설명

    var interestRate: Double, // 이자율 또는 예상 수익률

    var termInMonths: Int,    // 가입 기간 (월 단위)

    var minimumAmount: Double, // 최소 가입 금액

    var active: Boolean,      // 상품 활성화 여부

    val createdAt: LocalDateTime, // 생성일

    var updatedAt: LocalDateTime, // 수정일

    var tags: List<String> = listOf(), // 검색용 태그

    var featuredRank: Int? = null // 추천 상품 순위 (null이면 추천 아님)
) {
    companion object {
        /**
         * 새 상품 문서 생성 팩토리 메서드
         */
        fun of(
            productId: String,
            name: String,
            type: ProductType,
            description: String?,
            interestRate: Double,
            termInMonths: Int,
            minimumAmount: Double,
            active: Boolean,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
            tags: List<String> = listOf(),
            featuredRank: Int? = null
        ): ProductDocument {
            return ProductDocument(
                id = null,
                productId = productId,
                name = name,
                type = type,
                description = description,
                interestRate = interestRate,
                termInMonths = termInMonths,
                minimumAmount = minimumAmount,
                active = active,
                createdAt = createdAt,
                updatedAt = updatedAt,
                tags = tags,
                featuredRank = featuredRank
            )
        }
    }

    /**
     * 상품 정보 요약
     */
    override fun toString(): String {
        return "Product(id=$productId, name=$name, type=$type, rate=$interestRate%, term=$termInMonths months, active=$active)"
    }
}