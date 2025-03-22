package com.example.cqrs.infrastructure.eventstore.entity.enumerate

/**
 * 이벤트 처리 상태
 * 이벤트의 현재 처리 상태를 나타냄
 */
enum class EventStatus(
    val code: String,
    val description: String,
) {
    PENDING("PENDING", "이벤트 처리 대기 중"),
    PROCESSED("PROCESSED", "이벤트 처리 완료"),
    FAILED("FAILED", "이벤트 처리 실패"),
    RETRYING("RETRYING", "이벤트 재처리 중")
}