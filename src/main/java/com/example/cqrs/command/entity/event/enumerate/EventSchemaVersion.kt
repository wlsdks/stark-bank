package com.example.cqrs.command.entity.event.enumerate

/**
 * 스키마 버전
 *
 * @property version 버전
 * @property description 설명
 * @constructor Create empty Event schema version
 */
enum class EventSchemaVersion(
    val version: String,
    val description: String
) {
    V1_0("1.0", "버전 1.0"),
    V1_1("1.1", "버전 1.1")
}