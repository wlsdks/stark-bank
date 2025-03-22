package com.example.cqrs.command.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "financial_product")
@Entity
class ProductEntity private constructor(
    @Id
    val productId: String,   // 상품 ID

    @Column(name = "name", nullable = false)
    var name: String,        // 상품명

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ProductType,   // 상품 유형 (예금, 적금, 펀드 등)

    @Column(name = "description")
    var description: String?, // 상품 설명

    @Column(name = "interest_rate", nullable = false)
    var interestRate: Double, // 이자율 또는 예상 수익률

    @Column(name = "termInMonths", nullable = false)
    var termInMonths: Int,   // 가입 기간 (월 단위)

    @Column(name = "minimum_amount", nullable = false)
    var minimumAmount: Double, // 최소 가입 금액

    @Column(name = "active", nullable = false)
    var active: Boolean,     // 상품 활성화 여부

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime, // 생성일

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime?  // 수정일
) {

    // 상품 비활성화
    fun deactivate() {
        this.active = false
        this.updatedAt = LocalDateTime.now()
    }

    // 상품 정보 업데이트
    fun updateDetails(
        name: String,
        description: String?,
        interestRate: Double,
        termInMonths: Int,
        minimumAmount: Double
    ) {
        this.name = name
        this.description = description
        this.interestRate = interestRate
        this.termInMonths = termInMonths
        this.minimumAmount = minimumAmount
        this.updatedAt = LocalDateTime.now()
    }

    companion object {
        // factory method
        fun of(
            productId: String,
            name: String,
            type: ProductType,
            description: String?,
            interestRate: Double,
            termInMonths: Int,
            minimumAmount: Double
        ): ProductEntity {
            return ProductEntity(
                productId, name, type, description,
                interestRate, termInMonths, minimumAmount,
                true, LocalDateTime.now(), null
            )
        }
    }

}