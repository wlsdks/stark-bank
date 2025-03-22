package com.example.cqrs.infrastructure.eventstore.entity.enumerate

/**
 * 이벤트 처리 실패 상태 열거형
 */
enum class FailureStatus {
    PENDING,    // 처리 대기 중
    RETRYING,   // 재시도 중
    RESOLVED,   // 해결됨
    EXHAUSTED   // 최대 재시도 횟수 초과
}