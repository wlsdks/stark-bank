package com.example.cqrs.infrastructure.persistence.query.document

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "account")
class AccountDocument private constructor(
    @Id
    @Indexed(unique = true)
    val accountId: String, // 계좌

    var balance: Double,   // 잔액

    var lastUpdated: LocalDateTime // 마지막 업데이트 시간
) {

    // 잔액 변경 메서드
    fun changeBalance(newBalance: Double) {
        this.balance = newBalance
        this.lastUpdated = LocalDateTime.now()
    }

    // 마지막 업데이트 시간 변경 메서드
    fun changeLastUpdated(newLastUpdated: LocalDateTime) {
        this.lastUpdated = newLastUpdated
    }

    companion object {
        fun of(accountId: String, balance: Double, lastUpdated: LocalDateTime): AccountDocument {
            return AccountDocument(accountId, balance, lastUpdated)
        }
    }

    // MongoDB를 위한 기본 생성자 (protected 접근 수준에 해당)
    private constructor() : this("", 0.0, LocalDateTime.MIN)

}