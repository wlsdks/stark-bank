package com.example.cqrs.command.entity.event.metadata;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 스키마 버전 enum
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum EventSchemaVersion {

    V1_0("1.0", "버전 1.0"),
    V1_1("1.1", "버전 1.1"),;

    private final String version;
    private final String description;

}