package com.example.cqrs.interfaces.api.command.account.dto.response

import com.example.cqrs.infrastructure.eventstore.entity.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.entity.enumerate.EventSchemaVersion
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이벤트 메타데이터 응답")
data class EventMetadataResponse(
    @Schema(
        description = "상관 ID - 동일한 업무 흐름 내의 여러 이벤트를 그룹화하는 식별자",
        example = "c8a7f69d-27d1-4d66-9c69-14b41d4d1d15"
    )
    val correlationId: String? = null,

    @Schema(
        description = "원인 ID - 현재 이벤트를 발생시킨 이전 이벤트의 ID",
        example = "b1e3c487-9f24-4ac1-a2b7-5389c16e3f08"
    )
    val causationId: String? = null,

    @Schema(
        description = "작업을 수행한 사용자 ID",
        example = "user123"
    )
    val userId: String? = null,

    @Schema(
        description = "이벤트 데이터 구조의 버전",
        example = "V1_0"
    )
    val schemaVersion: EventSchemaVersion
) {
    companion object {
        fun from(eventMetadata: EventMetadata): EventMetadataResponse {
            return EventMetadataResponse(
                correlationId = eventMetadata.correlationId,
                causationId = eventMetadata.causationId,
                userId = eventMetadata.userId,
                schemaVersion = eventMetadata.schemaVersion
            )
        }
    }
}