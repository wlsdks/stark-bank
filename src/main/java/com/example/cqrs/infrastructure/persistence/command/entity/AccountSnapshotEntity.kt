package com.example.cqrs.infrastructure.persistence.command.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 계좌 스냅샷 엔티티
 * 특정 시점의 계좌 상태를 저장하는 스냅샷 (성능 최적화용)
 */
@Table(name = "account_snapshot")
@Entity
class AccountSnapshotEntity private constructor(
    @Id
    val accountId: String, // 계좌 ID (기본키)

    @Column(name = "balance", nullable = false)
    val balance: Double,   // 스냅샷 시점의 잔액

    @Column(name = "snapshot_date", nullable = false)
    val snapshotDate: LocalDateTime, // 스냅샷 생성 시간

    @Column(name = "last_event_id", nullable = false)
    val lastEventId: Long  // 스냅샷 포함된 마지막 이벤트 ID
) {
    companion object {
        /**
         * 스냅샷 생성 팩토리 메서드
         */
        fun of(
            accountId: String,
            balance: Double,
            snapshotDate: LocalDateTime,
            lastEventId: Long
        ): com.example.cqrs.infrastructure.persistence.command.entity.AccountSnapshotEntity {
            return com.example.cqrs.infrastructure.persistence.command.entity.AccountSnapshotEntity(
                accountId = accountId,
                balance = balance,
                snapshotDate = snapshotDate,
                lastEventId = lastEventId
            )
        }
    }
}