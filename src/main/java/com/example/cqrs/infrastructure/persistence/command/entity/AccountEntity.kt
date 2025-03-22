package com.example.cqrs.infrastructure.persistence.command.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "account")
@Entity
class AccountEntity private constructor(
    @Id
    val accountId: String, // 계좌 ID (기본키)

    @Column(name = "user_id", nullable = false)
    val userId: String,    // 계좌 소유자 ID

    @Column(name = "account_type", nullable = false)
    val accountType: String, // 계좌 유형 (예: 입출금, 적금, 예금)

    @Column(name = "balance")
    var balance: Double,   // 현재 잔액

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime, // 생성일

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime, // 수정일

    @Column(name = "status", nullable = false)
    var status: String     // 계좌 상태 (활성, 비활성, 정지 등)
) {
    /**
     * 잔액 변경 메서드
     * 모든 잔액 변경은 이 메서드를 통해 이루어져야 함
     */
    fun changeBalance(newBalance: Double) {
        this.balance = newBalance
        this.updatedAt = LocalDateTime.now()
    }

    /**
     * 출금 가능 여부 확인 메서드
     * 잔액이 출금액보다 작으면 예외 발생
     */
    fun checkAvailableWithdraw(amount: Double) {
        if (this.balance < amount) {
            throw IllegalArgumentException("잔액이 부족합니다: 현재 잔액 ${this.balance}, 요청 금액 $amount")
        }
    }

    /**
     * 계좌 상태 변경 메서드
     */
    fun changeStatus(newStatus: String) {
        this.status = newStatus
        this.updatedAt = LocalDateTime.now()
    }

    companion object {
        /**
         * 새 계좌 생성 팩토리 메서드
         */
        fun of(
            accountId: String,
            userId: String,
            accountType: String = "STANDARD",
            initialBalance: Double = 0.0
        ): com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity {
            val now = LocalDateTime.now()
            return com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity(
                accountId = accountId,
                userId = userId,
                accountType = accountType,
                balance = initialBalance,
                createdAt = now,
                updatedAt = now,
                status = "ACTIVE"
            )
        }
    }
}