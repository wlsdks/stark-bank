package com.example.cqrs.query.dto;

import com.example.cqrs.command.entity.event.metadata.EventMetadata;
import lombok.*;

/**
 * 이벤트 메타데이터에 대한 Response DTO입니다.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EventMetadataResponse {

    private String correlationId;
    private String causationId;
    private String userId;
    private String eventVersion;

    // factory method
    public static EventMetadataResponse from(EventMetadata metadata) {
        return EventMetadataResponse.builder()
                .correlationId(metadata.getCorrelationId())
                .causationId(metadata.getCausationId())
                .userId(metadata.getUserId())
                .eventVersion(metadata.getEventVersion())
                .build();
    }

}