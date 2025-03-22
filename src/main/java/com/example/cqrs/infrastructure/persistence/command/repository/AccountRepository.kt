package com.example.cqrs.infrastructure.persistence.command.repository

import com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 계좌 엔티티 리포지토리
 * 계좌의 현재 상태 관리
 */
interface AccountRepository : JpaRepository<AccountEntity, String> {
    fun findByUserId(userId: String): List<AccountEntity>
}
