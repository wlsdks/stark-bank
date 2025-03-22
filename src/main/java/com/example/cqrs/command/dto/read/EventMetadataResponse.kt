package com.example.cqrs.command.dto.read

import com.example.cqrs.command.entity.event.enumerate.EventSchemaVersion
import com.example.cqrs.command.entity.event.base.metadata.EventMetadata

data class EventMetadataResponse(
    val correlationId: String? = null,
    val causationId: String? = null,
    val userId: String? = null,
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
