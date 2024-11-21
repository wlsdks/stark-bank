package com.example.cqrs.service;

import com.example.cqrs.entity.write.event.base.BaseAccountEvent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 계좌 관련 이벤트를 저장하고 조회하는 이벤트 저장소의 인터페이스입니다.
 * 이벤트 소싱 패턴에서 이벤트 저장소 역할을 수행합니다.
 */
public interface AccountEventStore {

    void save(BaseAccountEvent event);

    List<BaseAccountEvent> getEvents(String accountId, LocalDateTime after);

    List<BaseAccountEvent> getAllEvents(String accountId);

    List<BaseAccountEvent> findByMetadataCorrelationId(String correlationId);

    List<BaseAccountEvent> findByMetadataUserId(String userId);

    long countEventsAfterDate(String accountId, LocalDateTime afterDate);

}
