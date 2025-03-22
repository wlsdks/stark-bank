package com.example.cqrs.command.repository

import com.example.cqrs.command.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 계좌 엔티티 리포지토리
 * 계좌의 현재 상태 관리
 */
interface AccountRepository : JpaRepository<AccountEntity, String> {
    // 필요한 경우 추가 쿼리 메서드를 정의할 수 있음
    // 예: 사용자별 계좌 찾기
    fun findByUserId(userId: String): List<AccountEntity>
}
