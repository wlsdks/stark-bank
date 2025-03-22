package com.example.cqrs.infrastructure.persistence.command.repository

import com.example.cqrs.infrastructure.persistence.command.entity.AccountSnapshotEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

/**
 * 계좌 스냅샷 리포지토리
 * 계좌 상태의 스냅샷 관리 (성능 최적화용)
 */
interface AccountSnapshotRepository : JpaRepository<AccountSnapshotEntity, String> {
    // 특정 날짜 이후의 최신 스냅샷 조회
    fun findByAccountIdAndSnapshotDateAfterOrderBySnapshotDateDesc(
        accountId: String, afterDate: LocalDateTime
    ): List<AccountSnapshotEntity>

    // 계좌별 최신 스냅샷 가져오기
    fun findTopByAccountIdOrderBySnapshotDateDesc(accountId: String): AccountSnapshotEntity?
}
