package com.example.cqrs.service.impl;

import com.example.cqrs.entity.read.AccountReadEntity;
import com.example.cqrs.entity.write.event.base.BaseAccountEvent;
import com.example.cqrs.repository.read.AccountReadRepository;
import com.example.cqrs.service.AccountEventStore;
import com.example.cqrs.service.AccountReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 계좌 조회 관련 서비스의 구현체입니다.
 * CQRS 패턴의 Query(읽기) 부분을 담당하며, 읽기 전용 트랜잭션으로 처리됩니다.
 * 계좌 정보 조회, 거래 이력 조회, 계좌 목록 조회 등의 기능을 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@Service
public class AccountReadServiceImpl implements AccountReadService {

    private final AccountReadRepository accountReadRepository;  // 읽기 모델 저장소
    private final AccountEventStore accountEventStore;          // 이벤트 저장소

    /**
     * 특정 계좌의 정보를 조회합니다.
     *
     * @param accountId 조회할 계좌의 ID
     * @return 계좌 정보 엔티티
     * @throws IllegalArgumentException 계좌를 찾을 수 없는 경우
     */
    @Override
    public AccountReadEntity getAccount(String accountId) {
        return accountReadRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));
    }

    /**
     * 특정 계좌의 전체 이벤트 이력을 조회합니다.
     * 계좌 생성부터 현재까지의 모든 거래 이벤트를 시간순으로 반환합니다.
     *
     * @param accountId 조회할 계좌의 ID
     * @return 계좌 관련 이벤트 목록
     */
    @Override
    public List<BaseAccountEvent> getAccountHistory(String accountId) {
        return accountEventStore.getAllEvents(accountId);
    }

    /**
     * 특정 사용자가 수행한 모든 거래 이벤트를 조회합니다.
     * 이벤트 메타데이터의 사용자 ID를 기준으로 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자 관련 이벤트 목록
     */
    @Override
    public List<BaseAccountEvent> getUserTransactions(String userId) {
        return accountEventStore.findByMetadataUserId(userId);
    }

    /**
     * 연관된 거래들을 조회합니다.
     * 동일한 correlationId를 가진 이벤트들을 그룹으로 조회합니다.
     * 예를 들어, 계좌 이체 시 출금과 입금 이벤트를 함께 조회할 수 있습니다.
     *
     * @param correlationId 연관 ID
     * @return 연관된 이벤트 목록
     */
    @Override
    public List<BaseAccountEvent> getRelatedTransactions(String correlationId) {
        return accountEventStore.findByMetadataCorrelationId(correlationId);
    }

    /**
     * 활성 상태인 모든 계좌의 ID 목록을 조회합니다.
     * 스케줄러나 관리 작업에서 사용됩니다.
     * 현재 시스템에 존재하는 모든 계좌를 대상으로 합니다.
     *
     * @return 활성 계좌 ID 목록
     */
    @Override
    public List<String> getActiveAccountIds() {
        log.debug("Fetching all active account IDs");

        List<String> accountIds = accountReadRepository.findAll()
                .stream()
                .map(AccountReadEntity::getAccountId)
                .collect(Collectors.toList());

        log.debug("Found {} active accounts", accountIds.size());
        return accountIds;
    }

}