package com.example.cqrs.command.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "account")
class AccountEntity private constructor(
    @Id
    val accountId: String, // 계좌

    @Column(name = "balance")
    var balance: Double    // 잔액
) {

    // 잔액 변경 메서드
    fun changeBalance(balance: Double) {
        this.balance = balance
    }

    // 출금 잔액 검증 메서드
    fun checkAvailableWithdraw(amount: Double) {
        if (this.balance < amount) {
            throw IllegalArgumentException("잔액이 부족합니다.")
        }
    }

    companion object {
        fun of(accountId: String, amount: Double): AccountEntity {
            return AccountEntity(accountId, amount)
        }
    }

}