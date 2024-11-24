package com.example.cqrs.command.entity.event;

import com.example.cqrs.command.entity.event.enumerate.EventStatus;
import com.example.cqrs.command.entity.event.metadata.EventMetadata;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "event_type")
@Getter
@Entity
@Table(name = "account_event_store")
public abstract class AbstractAccountEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountId; // 계좌 ID

    @Column(nullable = false)
    private LocalDateTime eventDate; // 이벤트 발생 일시

    @Column(nullable = true)
    private Double amount; // 이벤트 발생 금액

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.PENDING;  // 이벤트 처리 상태

    @Embedded
    private EventMetadata metadata; // 이벤트 메타데이터

    @Version
    private Long version; // JPA 낙관적 잠금용 버전

    protected AbstractAccountEventEntity(String accountId, LocalDateTime eventDate,
                                         Double amount, EventMetadata metadata) {
        this.accountId = accountId;
        this.eventDate = eventDate;
        this.amount = amount;
        this.metadata = metadata;
    }

    public void markAsProcessed() {
        this.status = EventStatus.PROCESSED;
    }

    public void markAsFailed() {
        this.status = EventStatus.FAILED;
    }

}