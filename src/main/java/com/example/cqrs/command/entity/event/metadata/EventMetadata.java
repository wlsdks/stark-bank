package com.example.cqrs.command.entity.event.metadata;

import com.example.cqrs.command.entity.event.enumerate.EventSchemaVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EventMetadata {

    @Column(nullable = false)
    private String correlationId;  // 연관 이벤트 그룹 ID

    private String causationId;    // 원인이 되는 이벤트 ID

    @Column(nullable = false)
    private String userId;         // 처리한 사용자 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventSchemaVersion schemaVersion;  // 이벤트 스키마 버전

    // factory method
    public static EventMetadata of(String correlationId, String causationId, String userId) {
        return EventMetadata.builder()
                .correlationId(correlationId)
                .causationId(causationId)
                .userId(userId)
                .schemaVersion(EventSchemaVersion.V1_0)
                .build();
    }

    // 특정 버전으로 생성하는 factory method
    public static EventMetadata of(String correlationId, String causationId,
                                   String userId, EventSchemaVersion version) {
        return EventMetadata.builder()
                .correlationId(correlationId)
                .causationId(causationId)
                .userId(userId)
                .schemaVersion(version)
                .build();
    }

}