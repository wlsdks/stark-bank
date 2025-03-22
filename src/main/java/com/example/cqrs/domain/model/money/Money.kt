package com.example.cqrs.domain.model.money

/**
 * 금액 값 객체 (Value Object)
 * 불변 객체로 금액 관련 로직 캡슐화
 */
class Money private constructor(
    val amount: Double
) {
    operator fun plus(other: Money): Money {
        return Money(this.amount + other.amount)
    }

    operator fun minus(other: Money): Money {
        return Money(this.amount - other.amount)
    }

    operator fun compareTo(other: Money): Int {
        return this.amount.compareTo(other.amount)
    }

    companion object {
        fun of(amount: Double): Money {
            // 금액 유효성 검증 로직 추가 가능
            return Money(amount)
        }

        val ZERO = Money(0.0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Money

        // 부동소수점 비교는 정확한 값 대신 근사치 비교
        return Math.abs(amount - other.amount) < 0.001
    }

    override fun hashCode(): Int {
        return amount.hashCode()
    }

    override fun toString(): String {
        return String.format("%.2f", amount)
    }

}
