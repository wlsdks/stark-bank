package com.example.cqrs.command.entity.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum EventStatus {

    PENDING("PENDING", "이벤트 처리 대기 중"),
    PROCESSED("PROCESSED", "이벤트 처리 완료"),
    FAILED("FAILED", "이벤트 처리 실패");

    private final String code;
    private final String description;

    // factory method
    public static EventStatus from(String code) {
        for (EventStatus status : EventStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown EventStatus code: " + code);
    }

}