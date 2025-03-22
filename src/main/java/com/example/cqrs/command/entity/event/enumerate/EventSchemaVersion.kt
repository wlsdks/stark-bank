package com.example.cqrs.command.entity.event.enumerate

/**
 * 이벤트 스키마 버전
 * 이벤트 데이터 구조가 변경될 때 버전 관리를 위함
 */
enum class EventSchemaVersion(
    val version: String,
    val description: String
) {
    V1_0("1.0", "기본 버전"),
    V1_1("1.1", "확장 필드 추가"),
    V2_0("2.0", "주요 구조 변경")
}
