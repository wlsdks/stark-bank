package com.example.cqrs.command.entity.event.enumerate

enum class EventStatus(
    val code: String,
    val description: String,
) {
    PENDING("PENDING", "이벤트 처리 대기 중"),
    PROCESSED("PROCESSED", "이벤트 처리 완료"),
    FAILED("FAILED", "이벤트 처리 실패");
}