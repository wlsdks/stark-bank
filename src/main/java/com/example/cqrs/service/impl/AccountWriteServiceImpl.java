package com.example.cqrs.service.impl;

import com.example.cqrs.domain.Account;
import com.example.cqrs.entity.write.AccountSnapshotEntity;
import com.example.cqrs.entity.write.event.AccountCreatedEvent;
import com.example.cqrs.entity.write.event.MoneyDepositedEvent;
import com.example.cqrs.entity.write.event.MoneyWithdrawnEvent;
import com.example.cqrs.entity.write.event.base.BaseAccountEvent;
import com.example.cqrs.entity.write.event.base.EventMetadata;
import com.example.cqrs.exception.ConcurrencyException;
import com.example.cqrs.repository.read.AccountReadRepository;
import com.example.cqrs.repository.write.AccountSnapshotRepository;
import com.example.cqrs.service.AccountEventStore;
import com.example.cqrs.service.AccountWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 계좌 관련 쓰기 작업을 처리하는 서비스 구현체입니다.
 * 이벤트 소싱 패턴을 사용하여 모든 상태 변경을 이벤트로 저장하고,
 * CQRS 패턴의 Command(쓰기) 부분을 담당합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountWriteServiceImpl implements AccountWriteService {

    private final AccountEventStore accountEventStore;            // 이벤트 저장소
    private final AccountSnapshotRepository snapshotRepository;   // 스냅샷 저장소
    private final AccountReadRepository accountReadRepository;    // 읽기 모델 저장소
    private final ApplicationEventPublisher eventPublisher;       // 이벤트 발행기

    private static final int SNAPSHOT_THRESHOLD = 100; // 스냅샷 생성 기준 이벤트 수

    /**
     * 새로운 계좌를 생성합니다.
     * 계좌 생성 이벤트를 저장하고 발행합니다.
     *
     * @param accountId 생성할 계좌 ID
     * @param userId    요청한 사용자 ID
     * @throws IllegalArgumentException 계좌가 이미 존재하는 경우
     */
    @Transactional
    @Override
    public void createAccount(String accountId, String userId) {
        if (accountReadRepository.existsById(accountId)) {
            throw new IllegalArgumentException("이미 존재하는 계좌입니다.");
        }

        EventMetadata metadata = createEventMetadata(userId, null);
        AccountCreatedEvent event = new AccountCreatedEvent(
                accountId,
                LocalDateTime.now(),
                0.0,
                metadata
        );

        accountEventStore.save(event);
        eventPublisher.publishEvent(event);
        checkAndSaveSnapshot(accountId);
    }

    /**
     * 계좌에 입금합니다.
     * 입금 이벤트를 저장하고 발행합니다.
     *
     * @param accountId 입금할 계좌 ID
     * @param amount    입금액
     * @param userId    요청한 사용자 ID
     * @throws IllegalArgumentException 금액이 유효하지 않은 경우
     * @throws ConcurrencyException     동시성 충돌이 발생한 경우
     */
    @Transactional
    @Override
    public void depositMoney(String accountId, double amount, String userId) {
        validateAmount(amount);
        Account account = loadAccount(accountId);

        EventMetadata metadata = createEventMetadata(userId, null);
        MoneyDepositedEvent event = new MoneyDepositedEvent(
                accountId,
                LocalDateTime.now(),
                amount,
                metadata
        );

        try {
            accountEventStore.save(event);
            eventPublisher.publishEvent(event);
            checkAndSaveSnapshot(accountId);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.");
        }
    }

    /**
     * 계좌에서 출금합니다.
     * 출금 이벤트를 저장하고 발행합니다.
     *
     * @param accountId 출금할 계좌 ID
     * @param amount    출금액
     * @param userId    요청한 사용자 ID
     * @throws IllegalArgumentException 금액이 유효하지 않거나 잔액이 부족한 경우
     * @throws ConcurrencyException     동시성 충돌이 발생한 경우
     */
    @Transactional
    @Override
    public void withdrawMoney(String accountId, double amount, String userId) {
        validateAmount(amount);
        Account account = loadAccount(accountId);
        account.checkAvailableWithdraw(amount);

        EventMetadata metadata = createEventMetadata(userId, null);
        MoneyWithdrawnEvent event = new MoneyWithdrawnEvent(
                accountId,
                LocalDateTime.now(),
                amount,
                metadata
        );

        try {
            accountEventStore.save(event);
            eventPublisher.publishEvent(event);
            checkAndSaveSnapshot(accountId);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.");
        }
    }

    /**
     * 이벤트 메타데이터를 생성합니다.
     *
     * @param userId      사용자 ID
     * @param causationId 원인이 되는 이벤트 ID (없을 수 있음)
     * @return 생성된 메타데이터
     */
    private EventMetadata createEventMetadata(String userId, String causationId) {
        return EventMetadata.builder()
                .correlationId(UUID.randomUUID().toString())
                .causationId(causationId)
                .userId(userId)
                .eventVersion("1.0")
                .build();
    }

    /**
     * 계좌의 현재 상태를 로드합니다.
     *
     * @param accountId 계좌 ID
     * @return 계좌 도메인 객체
     */
    private Account loadAccount(String accountId) {
        double balance = calculateCurrentBalance(accountId);
        return Account.of(accountId, balance);
    }

    /**
     * 계좌의 현재 잔액을 계산합니다.
     * 가장 최근 스냅샷부터 현재까지의 이벤트를 적용하여 계산합니다.
     *
     * @param accountId 계좌 ID
     * @return 현재 잔액
     */
    private double calculateCurrentBalance(String accountId) {
        AccountSnapshotEntity snapshot = snapshotRepository.findById(accountId)
                .orElse(null);

        LocalDateTime fromDate;
        double balance;

        if (snapshot != null) {
            balance = snapshot.getBalance();
            fromDate = snapshot.getSnapshotDate();
        } else {
            balance = 0.0;
            fromDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        }

        List<BaseAccountEvent> events = accountEventStore.getEvents(accountId, fromDate);

        for (BaseAccountEvent event : events) {
            if (event instanceof MoneyDepositedEvent) {
                balance += event.getAmount();
            } else if (event instanceof MoneyWithdrawnEvent) {
                balance -= event.getAmount();
            }
        }

        return balance;
    }

    /**
     * 금액의 유효성을 검증합니다.
     *
     * @param amount 검증할 금액
     * @throws IllegalArgumentException 금액이 0 이하인 경우
     */
    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
    }

    /**
     * 스냅샷 저장이 필요한지 확인하고 필요한 경우 저장합니다.
     *
     * @param accountId 계좌 ID
     */
    private void checkAndSaveSnapshot(String accountId) {
        AccountSnapshotEntity lastSnapshot = snapshotRepository.findById(accountId).orElse(null);
        LocalDateTime fromDate = lastSnapshot != null ?
                lastSnapshot.getSnapshotDate() :
                LocalDateTime.of(1970, 1, 1, 0, 0);

        long eventCount = accountEventStore.countEventsAfterDate(accountId, fromDate);

        if (eventCount >= SNAPSHOT_THRESHOLD) {
            saveSnapshot(accountId);
        }
    }

    /**
     * 현재 상태의 스냅샷을 저장합니다.
     *
     * @param accountId 계좌 ID
     */
    private void saveSnapshot(String accountId) {
        double currentBalance = calculateCurrentBalance(accountId);
        AccountSnapshotEntity snapshot = AccountSnapshotEntity.of(
                accountId,
                currentBalance,
                LocalDateTime.now()
        );
        snapshotRepository.save(snapshot);
    }

}