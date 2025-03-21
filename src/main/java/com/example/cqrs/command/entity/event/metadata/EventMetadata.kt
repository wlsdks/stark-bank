package com.example.cqrs.command.entity.event.metadata

import com.example.cqrs.command.entity.event.enumerate.EventSchemaVersion
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class EventMetadata private constructor(
    @Column(name = "correlationId", nullable = false)
    val correlationId: String, // 연관 이벤트 그룹 ID

    @Column(name = "causationId")
    val causationId: String?,  // 원인이 되는 이벤트 ID

    @Column(name = "userId", nullable = false)
    val userId: String,        // 처리한 사용자 ID

    @Column(name = "schemaVersion", nullable = false)
    @Enumerated(EnumType.STRING)
    val schemaVersion: EventSchemaVersion // 이벤트 스키마 버전
) {

    companion object {
        // factory method
        fun of(correlationId: String, causationId: String?, userId: String): EventMetadata {
            return EventMetadata(
                correlationId = correlationId,
                causationId = causationId,
                userId = userId,
                schemaVersion = EventSchemaVersion.V1_0
            )
        }

        // 특정 버전으로 생성하는 factory method
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