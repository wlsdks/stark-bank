package com.example.cqrs.service;

import com.example.cqrs.entity.write.event.base.AbstractAccountEvent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 계좌 관련 이벤트를 저장하고 조회하는 이벤트 저장소의 인터페이스입니다.
 * 이벤트 소싱 패턴에서 이벤트 저장소 역할을 수행합니다.
 */
public interface AccountEventStore {

    void save(AbstractAccountEvent event);

    List<AbstractAccountEvent> getEvents(String accountId, LocalDateTime after);

    List<AbstractAccountEvent> getAllEvents(String accountId);

    List<AbstractAccountEvent> findByMetadataCorrelationId(String correlationId);

    List<AbstractAccountEvent> findByMetadataUserId(String userId);

    long countEventsAfterDate(String accountId, LocalDateTime afterDate);

}
