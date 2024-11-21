package com.example.cqrs.entity.read.write.event.base;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EventMetadata {

    private String correlationId;  // 연관 이벤트 그룹 ID
    private String causationId;    // 원인이 되는 이벤트 ID
    private String userId;         // 처리한 사용자 ID
    private String eventVersion;   // 이벤트 버전

    // factory method
    public static EventMetadata of(String correlationId, String causationId, String userId, String eventVersion) {
        return new EventMetadata(correlationId, causationId, userId, eventVersion);
    }

}