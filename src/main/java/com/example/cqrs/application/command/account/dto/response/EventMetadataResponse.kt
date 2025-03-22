package com.example.cqrs.application.command.account.dto.response

import com.example.cqrs.infrastructure.eventstore.enumerate.EventSchemaVersion
import com.example.cqrs.infrastructure.eventstore.base.metadata.EventMetadata

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
