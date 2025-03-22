package com.example.cqrs.infrastructure.persistence.command.entity

/**
 * 상품 유형 enum
 */
enum class ProductType(
    val code: String,
    val description: String
) {
    SAVINGS_ACCOUNT("SA", "예금 계좌"),
    TIME_DEPOSIT("TD", "정기 예금"),
    INSTALLMENT_SAVINGS("IS", "적금"),
    FUND("FD", "펀드"),
    STOCK("ST", "주식"),
    BOND("BD", "채권"),
    ETF("EF", "상장지수펀드"),
    PENSION("PN", "연금"),
    INSURANCE("IN", "보험"),
    LOAN("LN", "대출")
}