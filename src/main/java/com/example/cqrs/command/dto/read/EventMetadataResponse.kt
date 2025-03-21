package com.example.cqrs.command.dto.read

import com.example.cqrs.command.entity.event.enumerate.EventSchemaVersion

data class EventMetadataResponse(
    val correlationId: String? = null,
    val causationId: String? = null,
    val userId: String? = null,
    val schemaVersion: EventSchemaVersion
) {
    companion object {
        fun from(
            correlationId: String?,
            causationId: String?,
            userId: String?,
            schemaVersion: EventSchemaVersion
        ): EventMetadataResponse {
            return EventMetadataResponse(correlationId, causationId, userId, schemaVersion)
        }
    }
}
