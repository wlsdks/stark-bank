package com.example.cqrs.command.usecase;

import com.example.cqrs.command.entity.event.AbstractAccountEventEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 계좌 관련 이벤트를 저장하고 조회하는 이벤트 저장소의 인터페이스입니다.
 * 이벤트 소싱 패턴에서 이벤트 저장소 역할을 수행합니다.
 */
public interface AccountEventStoreUseCase {

    void save(AbstractAccountEventEntity event);

    void saveEventStatus(AbstractAccountEventEntity event);

    List<AbstractAccountEventEntity> getEvents(String accountId, LocalDateTime after);

    List<AbstractAccountEventEntity> getAllEvents(String accountId);

    List<AbstractAccountEventEntity> findByMetadataCorrelationId(String correlationId);

    List<AbstractAccountEventEntity> findByMetadataUserId(String userId);

    long countEventsAfterDate(String accountId, LocalDateTime afterDate);

}
