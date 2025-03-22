package com.example.cqrs.domain

import com.example.cqrs.common.exception.InsufficientBalanceException
import java.time.LocalDateTime


/**
 * 계좌 도메인 모델
 * 비즈니스 로직을 포함한 핵심 도메인 객체 (Aggregate Root)
 */
class Account private constructor(
    val accountId: String, // 계좌 ID
    val userId: String,    // 사용자 ID
    private var balance: Double,   // 잔액
    val createdAt: LocalDateTime,  // 생성일
    private var updatedAt: LocalDateTime // 수정일
) {
    /**
     * 현재 잔액 조회 (값 객체로 변환하여 반환)
     */
    fun getBalance(): Money {
        return Money.of(balance)
    }

    /**
     * 입금 처리
     * @param amount 입금액
     * @return 입금 후 잔액
     */
    fun deposit(amount: Double): Money {
        validateAmount(amount)
        balance += amount
        updatedAt = LocalDateTime.now()
        return Money.of(balance)
    }

    /**
     * 출금 처리
     * @param amount 출금액
     * @return 출금 후 잔액
     * @throws InsufficientBalanceException 잔액 부족 시
     */
    fun withdraw(amount: Double): Money {
        validateAmount(amount)
        if (balance < amount) {
            throw InsufficientBalanceException(
                "잔액이 부족합니다. 현재 잔액: $balance, 요청 금액: $amount"
            )
        }

        balance -= amount
        updatedAt = LocalDateTime.now()
        return Money.of(balance)
    }

    /**
     * 금액 유효성 검증
     */
    private fun validateAmount(amount: Double) {
        if (amount <= 0) {
            throw IllegalArgumentException("금액은 0보다 커야 합니다: $amount")
        }
    }

    /**
     * 현재 상태 복제 (이벤트 재생성 등에 사용)
     */
    fun copy(): Account {
        return Account(accountId, userId, balance, createdAt, updatedAt)
    }

    companion object {
        /**
         * 새 계좌 생성 팩토리 메서드
         */
        fun create(accountId: String, userId: String): Account {
            val now = LocalDateTime.now()
            return Account(accountId, userId, 0.0, now, now)
        }

        /**
         * 기존 데이터로 계좌 객체 생성 (이벤트 소싱에서 재구성 등에 사용)
         */
        fun of(
            accountId: String,
            userId: String,
            balance: Double,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Account {
            return Account(accountId, userId, balance, createdAt, updatedAt)
        }
    }
}