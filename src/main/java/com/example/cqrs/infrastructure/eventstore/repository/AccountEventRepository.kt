package com.example.cqrs.infrastructure.eventstore.repository

import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

/**
 * 계좌 이벤트 리포지토리
 * 모든 계좌 관련 이벤트 저장 및 조회
 */
interface AccountEventRepository : JpaRepository<AccountEventBaseEntity, Long> {
    // 계좌 ID와 날짜 기준 이벤트 조회 (시간순 정렬)
    fun findByAccountIdAndEventDateAfterOrderByEventDateAsc(
        accountId: String, afterDate: LocalDateTime
    ): List<AccountEventBaseEntity>

    // 계좌별 전체 이벤트 조회 (최신순 정렬)
    fun findByAccountIdOrderByEventDateDesc(accountId: String): List<AccountEventBaseEntity>

    // 계좌별 전체 이벤트 조회 (시간순 정렬)
    fun findByAccountIdOrderByEventDateAsc(accountId: String): List<AccountEventBaseEntity>

    // 연관 ID로 이벤트 그룹 조회
    fun findByMetadataCorrelationId(correlationId: String): List<AccountEventBaseEntity>

    // 사용자별 이벤트 조회 (최신순 정렬)
    fun findByMetadataUserIdOrderByEventDateDesc(userId: String): List<AccountEventBaseEntity>

    // 특정 날짜 이후 이벤트 개수 조회
    fun countByAccountIdAndEventDateAfter(accountId: String, afterDate: LocalDateTime): Long
}