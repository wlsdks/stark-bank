package com.example.cqrs.command.service;

import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import com.example.cqrs.command.repository.AccountEventStoreRepository;
import com.example.cqrs.command.useCase.AccountEventStoreUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 계좌 이벤트 저장소의 구현체입니다.
 * 이벤트의 저장, 조회 및 유효성 검증을 담당합니다.
 */
@RequiredArgsConstructor
@Service
public class AccountEventStoreService implements AccountEventStoreUseCase {

    private final AccountEventStoreRepository eventRepository;

    /**
     * 새로운 이벤트를 저장합니다.
     * 저장 전 이벤트의 유효성을 검증합니다.
     *
     * @param event 저장할 이벤트
     * @throws IllegalArgumentException 필수 필드가 누락된 경우
     * @throws IllegalStateException    이벤트 시간 순서가 올바르지 않은 경우
     */
    @Override
    @Transactional
    public void save(AbstractAccountEvent event) {
        validateEvent(event);
        eventRepository.save(event);
    }

    /**
     * 특정 날짜 이후의 계좌 이벤트를 조회합니다.
     *
     * @param accountId 계좌 ID
     * @param after     조회 시작 날짜
     * @return 시간순으로 정렬된 이벤트 목록
     */
    @Override
    public List<AbstractAccountEvent> getEvents(String accountId, LocalDateTime after) {
        return eventRepository.findByAccountIdAndEventDateAfterOrderByEventDateAsc(
                accountId, after);
    }

    /**
     * 계좌의 모든 이벤트를 시간순으로 조회합니다.
     *
     * @param accountId 계좌 ID
     * @return 전체 이벤트 목록
     */
    @Override
    public List<AbstractAccountEvent> getAllEvents(String accountId) {
        return eventRepository.findByAccountIdOrderByEventDateAsc(accountId);
    }

    /**
     * 연관 ID로 이벤트를 조회합니다.
     * 동일한 거래에 속한 이벤트들을 그룹화하여 조회할 때 사용됩니다.
     *
     * @param correlationId 연관 ID
     * @return 연관된 이벤트 목록
     */
    @Override
    public List<AbstractAccountEvent> findByMetadataCorrelationId(String correlationId) {
        return eventRepository.findByMetadataCorrelationId(correlationId);
    }

    /**
     * 사용자 ID로 이벤트를 조회합니다.
     * 특정 사용자의 모든 거래 이력을 조회할 때 사용됩니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 이벤트 목록
     */
    @Override
    public List<AbstractAccountEvent> findByMetadataUserId(String userId) {
        return eventRepository.findByMetadataUserIdOrderByEventDateDesc(userId);
    }

    /**
     * 특정 날짜 이후의 이벤트 수를 계산합니다.
     * 주로 스냅샷 생성 여부를 결정할 때 사용됩니다.
     *
     * @param accountId 계좌 ID
     * @param afterDate 기준 날짜
     * @return 이벤트 수
     */
    @Override
    public long countEventsAfterDate(String accountId, LocalDateTime afterDate) {
        return eventRepository.countByAccountIdAndEventDateAfter(accountId, afterDate);
    }

    /**
     * 이벤트의 기본 유효성을 검증합니다.
     * 필수 필드 존재 여부와 이벤트 시간 순서를 확인합니다.
     *
     * @param event 검증할 이벤트
     * @throws IllegalArgumentException 필수 필드가 누락된 경우
     * @throws IllegalStateException    이벤트 시간 순서가 올바르지 않은 경우
     */
    private void validateEvent(AbstractAccountEvent event) {
        if (event.getAccountId() == null || event.getEventDate() == null) {
            throw new IllegalArgumentException("필수 필드가 누락되었습니다.");
        }

        List<AbstractAccountEvent> existingEvents = eventRepository
                .findByAccountIdOrderByEventDateDesc(event.getAccountId());

        if (!existingEvents.isEmpty()) {
            LocalDateTime lastEventTime = existingEvents.get(0).getEventDate();
            if (!event.getEventDate().isAfter(lastEventTime)) {
                throw new IllegalStateException("이벤트의 시간 순서가 올바르지 않습니다.");
            }
        }

        validateEventMetadata(event);
    }

    /**
     * 이벤트 메타데이터의 유효성을 검증합니다.
     *
     * @param event 검증할 이벤트
     * @throws IllegalArgumentException 메타데이터가 유효하지 않은 경우
     */
    private void validateEventMetadata(AbstractAccountEvent event) {
        if (event.getMetadata() == null) {
            throw new IllegalArgumentException("이벤트 메타데이터가 누락되었습니다.");
        }
        if (event.getMetadata().getUserId() == null) {
            throw new IllegalArgumentException("사용자 ID가 누락되었습니다.");
        }
        if (event.getMetadata().getEventVersion() == null) {
            throw new IllegalArgumentException("이벤트 버전이 누락되었습니다.");
        }
    }

}