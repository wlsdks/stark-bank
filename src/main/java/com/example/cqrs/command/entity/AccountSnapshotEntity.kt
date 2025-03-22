package com.example.cqrs.command.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "account_snapshot")
@Entity
class AccountSnapshotEntity private constructor(
    @Id
    val accountId: String, // 계좌의 기본 ID

    @Column(name = "balance")
    val balance: Double,   // 계좌의 잔액

    @Column(name = "snapshot_date")
    val snapshotDate: LocalDateTime // 스냅샷 생성 시간
) {

    companion object {
        fun of(accountId: String, balance: Double, snapshotDate: LocalDateTime): AccountSnapshotEntity {
            return AccountSnapshotEntity(accountId, balance, snapshotDate)
        }
    }

}