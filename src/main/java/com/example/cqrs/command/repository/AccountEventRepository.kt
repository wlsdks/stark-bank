package com.example.cqrs.command.repository

import com.example.cqrs.command.entity.event.base.AccountEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

/**
 * 계좌 이벤트 리포지토리
 * 모든 계좌 관련 이벤트 저장 및 조회
 */
interface AccountEventRepository : JpaRepository<AccountEvent, Long> {
    // 계좌 ID와 날짜 기준 이벤트 조회 (시간순 정렬)
    fun findByAccountIdAndEventDateAfterOrderByEventDateAsc(
        accountId: String, afterDate: LocalDateTime
    ): List<AccountEvent>

    // 계좌별 전체 이벤트 조회 (최신순 정렬)
    fun findByAccountIdOrderByEventDateDesc(accountId: String): List<AccountEvent>

    // 계좌별 전체 이벤트 조회 (시간순 정렬)
    fun findByAccountIdOrderByEventDateAsc(accountId: String): List<AccountEvent>

    // 연관 ID로 이벤트 그룹 조회
    fun findByMetadataCorrelationId(correlationId: String): List<AccountEvent>

    // 사용자별 이벤트 조회 (최신순 정렬)
    fun findByMetadataUserIdOrderByEventDateDesc(userId: String): List<AccountEvent>

    // 특정 날짜 이후 이벤트 개수 조회
    fun countByAccountIdAndEventDateAfter(accountId: String, afterDate: LocalDateTime): Long
}