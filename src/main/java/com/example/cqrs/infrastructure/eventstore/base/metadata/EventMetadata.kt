package com.example.cqrs.infrastructure.eventstore.base.metadata

import com.example.cqrs.infrastructure.eventstore.enumerate.EventSchemaVersion
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

/**
 * 이벤트 메타데이터
 * 이벤트의 추적 및 관리를 위한 정보를 담고 있음
 */
@Embeddable
class EventMetadata private constructor(
    @Column(name = "correlation_id", nullable = false)
    val correlationId: String, // 연관 이벤트 그룹 ID (동일 트랜잭션의 이벤트 그룹핑)

    @Column(name = "causation_id")
    val causationId: String?,  // 원인이 되는 이벤트 ID (이벤트 체인 추적)

    @Column(name = "user_id", nullable = false)
    val userId: String,        // 작업을 수행한 사용자 ID

    @Column(name = "schema_version", nullable = false)
    @Enumerated(EnumType.STRING)
    val schemaVersion: EventSchemaVersion // 이벤트 스키마 버전 (이벤트 구조 변경에 대응)
) {
    companion object {
        /**
         * 기본 버전으로 메타데이터 생성
         */
        fun of(correlationId: String, causationId: String?, userId: String): EventMetadata {
            return EventMetadata(
                correlationId = correlationId,
                causationId = causationId,
                userId = userId,
                schemaVersion = EventSchemaVersion.V1_0
            )
        }

        /**
         * 특정 버전으로 메타데이터 생성
         */
        fun of(
            correlationId: String,
            causationId: String?,
            userId: String,
            version: EventSchemaVersion
        ): EventMetadata {
            return EventMetadata(
                correlationId = correlationId,
                causationId = causationId,
                userId = userId,
                schemaVersion = version
            )
        }
    }
}