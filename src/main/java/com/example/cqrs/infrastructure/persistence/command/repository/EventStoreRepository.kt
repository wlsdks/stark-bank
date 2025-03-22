package com.example.cqrs.infrastructure.persistence.command.repository

import com.example.cqrs.infrastructure.eventstore.base.BaseEvent
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 기본 이벤트 리포지토리
 * 모든 이벤트 타입에 대한 공통 조회 기능 제공
 */
interface EventStoreRepository : JpaRepository<BaseEvent, Long> {

    /**
     * 특정 상관 ID로 모든 연관 이벤트 조회
     */
    fun findByMetadataCorrelationId(correlationId: String): List<BaseEvent>

    /**
     * 특정 사용자의 모든 이벤트 시간순 조회
     */
    fun findByMetadataUserIdOrderByEventDateDesc(userId: String): List<BaseEvent>

}