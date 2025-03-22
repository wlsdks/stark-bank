package com.example.cqrs.domain


/**
 * 계좌의 현재 상태를 나타내는 도메인 모델입니다.
 * 불변 객체로 설계되어 있으며, 상태 변경 시 새로운 객체를 생성합니다.
 * 이벤트 소싱에서 애그리게이트(Aggregate)로서의 역할을 수행합니다.
 */
class Account private constructor(
    val accountId: String, // 계좌의 기본 ID
    val balance: Double    // 계좌의 잔액
) {
    // 출금 잔액 검증 메서드
    fun checkAvailableWithdraw(amount: Double) {
        if (this.balance < amount) {
            throw IllegalArgumentException("잔액이 부족합니다.")
        }
    }

    companion object {
        // factory method
        fun of(accountId: String, balance: Double): Account {
            return Account(accountId, balance)
        }
    }

    // 기본 생성자 (protected 접근 수준에 해당)
    private constructor() : this("", 0.0)
}