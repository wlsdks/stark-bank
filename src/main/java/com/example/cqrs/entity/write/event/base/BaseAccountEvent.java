package com.example.cqrs.entity.write.event.base;

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
@Table(name = "account_event")
public abstract class BaseAccountEvent implements AccountEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = true)
    private Double amount;

    @Version
    private Long version;

    @Embedded
    private EventMetadata metadata;

    protected BaseAccountEvent(String accountId, LocalDateTime eventDate,
                             Double amount, EventMetadata metadata) {
        this.accountId = accountId;
        this.eventDate = eventDate;
        this.amount = amount;
        this.metadata = metadata;
    }

}