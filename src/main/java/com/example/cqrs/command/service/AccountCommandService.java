package com.example.cqrs.command.service;

import com.example.cqrs.command.dto.CreateAccountRequest;
import com.example.cqrs.command.dto.DepositRequest;
import com.example.cqrs.command.dto.TransferRequest;
import com.example.cqrs.command.dto.WithdrawRequest;
import com.example.cqrs.command.entity.AccountSnapshot;
import com.example.cqrs.command.entity.event.AbstractAccountEvent;
import com.example.cqrs.command.entity.event.AccountCreatedEvent;
import com.example.cqrs.command.entity.event.MoneyDepositedEvent;
import com.example.cqrs.command.entity.event.MoneyWithdrawnEvent;
import com.example.cqrs.command.entity.event.metadata.EventMetadata;
import com.example.cqrs.command.repository.AccountSnapshotRepository;
import com.example.cqrs.command.usecase.AccountCommandUseCase;
import com.example.cqrs.command.usecase.AccountEventStoreUseCase;
import com.example.cqrs.common.exception.ConcurrencyException;
import com.example.cqrs.domain.Account;
import com.example.cqrs.query.repository.AccountViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 계좌 관련 Command(쓰기) 작업을 처리하는 서비스 구현체입니다.
 * 이벤트 소싱 패턴을 사용하여 모든 상태 변경을 이벤트로 저장하고,
 * CQRS 패턴의 Command(쓰기) 부분을 담당합니다.
 * <p>
 * 주요 기능:
 * - 계좌 생성
 * - 입금 처리
 * - 출금 처리
 * - 계좌 이체
 * - 스냅샷 관리
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountCommandService implements AccountCommandUseCase {

    private final AccountEventStoreUseCase accountEventStoreUseCase;    // 이벤트 저장소 UseCase
    private final AccountSnapshotRepository snapshotRepository;         // 스냅샷 저장소
    private final AccountViewRepository accountViewRepository;          // 읽기 모델 저장소
    private final ApplicationEventPublisher eventPublisher;             // 이벤트 발행기

    // 스냅샷 생성 기준 이벤트 수
    private static final int SNAPSHOT_THRESHOLD = 100;

    /**
     * 새로운 계좌를 생성합니다.
     * 계좌 생성 이벤트를 저장하고 발행합니다.
     *
     * @param request 계좌 생성 요청 DTO (계좌ID, 사용자ID 포함)
     * @throws IllegalArgumentException 계좌가 이미 존재하는 경우
     */
    @Transactional
    @Override
    public void createAccount(CreateAccountRequest request) {
        // 계좌 중복 검사
        if (accountViewRepository.existsById(request.getAccountId())) {
            throw new IllegalArgumentException("이미 존재하는 계좌입니다.");
        }

        // 계좌 생성 이벤트 생성 및 저장
        String correlationId = UUID.randomUUID().toString();
        EventMetadata eventMetadata = EventMetadata.of(correlationId, null, request.getUserId());
        AccountCreatedEvent event = AccountCreatedEvent.of(
                request.getAccountId(),
                LocalDateTime.now(),
                0.0,
                eventMetadata
        );

        accountEventStoreUseCase.save(event);
        eventPublisher.publishEvent(event);
        checkAndSaveSnapshot(request.getAccountId());
    }

    /**
     * 계좌에 입금합니다.
     * 입금 이벤트를 저장하고 발행합니다.
     *
     * @param request 입금 요청 DTO (계좌ID, 입금액, 사용자ID 포함)
     * @throws IllegalArgumentException 금액이 유효하지 않은 경우
     * @throws ConcurrencyException     동시성 충돌이 발생한 경우
     */
    @Transactional
    @Override
    public void depositMoney(DepositRequest request) {
        validateAmount(request.getAmount());
        Account account = loadAccount(request.getAccountId());

        // 입금 이벤트 생성 및 저장
        String correlationId = UUID.randomUUID().toString();
        EventMetadata eventMetadata = EventMetadata.of(correlationId, null, request.getUserId());
        MoneyDepositedEvent event = MoneyDepositedEvent.of(
                request.getAccountId(),
                LocalDateTime.now(),
                request.getAmount(),
                eventMetadata
        );

        try {
            accountEventStoreUseCase.save(event);
            eventPublisher.publishEvent(event);
            checkAndSaveSnapshot(request.getAccountId());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.");
        }
    }

    /**
     * 계좌에서 출금합니다.
     * 출금 이벤트를 저장하고 발행합니다.
     *
     * @param request 출금 요청 DTO (계좌ID, 출금액, 사용자ID 포함)
     * @throws IllegalArgumentException 금액이 유효하지 않거나 잔액이 부족한 경우
     * @throws ConcurrencyException     동시성 충돌이 발생한 경우
     */
    @Transactional
    @Override
    public void withdrawMoney(WithdrawRequest request) {
        validateAmount(request.getAmount());
        Account account = loadAccount(request.getAccountId());
        account.checkAvailableWithdraw(request.getAmount());

        // 출금 이벤트 생성 및 저장
        String correlationId = UUID.randomUUID().toString();
        EventMetadata eventMetadata = EventMetadata.of(correlationId, null, request.getUserId());
        MoneyWithdrawnEvent event = MoneyWithdrawnEvent.of(
                request.getAccountId(),
                LocalDateTime.now(),
                request.getAmount(),
                eventMetadata
        );

        try {
            accountEventStoreUseCase.save(event);
            eventPublisher.publishEvent(event);
            checkAndSaveSnapshot(request.getAccountId());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.");
        }
    }

    /**
     * 계좌 이체를 수행합니다.
     * 출금과 입금 이벤트를 하나의 거래로 묶어 처리합니다.
     *
     * @param request 이체 요청 DTO
     * @throws IllegalArgumentException 금액이 유효하지 않거나 잔액이 부족한 경우
     * @throws ConcurrencyException     동시성 충돌이 발생한 경우
     */
    @Transactional
    @Override
    public void transfer(TransferRequest request) {
        validateAmount(request.getAmount());
        Account fromAccount = loadAccount(request.getFromAccountId());
        fromAccount.checkAvailableWithdraw(request.getAmount());

        // 하나의 거래를 위한 correlation ID 생성
        String correlationId = UUID.randomUUID().toString();

        // 출금 이벤트 생성
        EventMetadata withdrawMetadata = EventMetadata.of(correlationId, null, request.getUserId());
        MoneyWithdrawnEvent withdrawEvent = MoneyWithdrawnEvent.of(
                request.getFromAccountId(),
                LocalDateTime.now(),
                request.getAmount(),
                withdrawMetadata
        );

        // 입금 이벤트 생성 (출금 이벤트를 원인으로 지정)
        EventMetadata depositMetadata = EventMetadata.of(correlationId, String.valueOf(withdrawEvent.getId()), request.getUserId());
        MoneyDepositedEvent depositEvent = MoneyDepositedEvent.of(
                request.getToAccountId(),
                LocalDateTime.now(),
                request.getAmount(),
                depositMetadata
        );

        try {
            accountEventStoreUseCase.save(withdrawEvent);
            accountEventStoreUseCase.save(depositEvent);
            eventPublisher.publishEvent(withdrawEvent);
            eventPublisher.publishEvent(depositEvent);
            checkAndSaveSnapshot(request.getFromAccountId());
            checkAndSaveSnapshot(request.getToAccountId());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.");
        }
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
        // 최근 스냅샷 조회
        AccountSnapshot snapshot = snapshotRepository.findById(accountId)
                .orElse(null);

        LocalDateTime fromDate;
        double balance;

        // 스냅샷이 있으면 스냅샷 시점부터, 없으면 처음부터 계산
        if (snapshot != null) {
            balance = snapshot.getBalance();
            fromDate = snapshot.getSnapshotDate();
        } else {
            balance = 0.0;
            fromDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        }

        // 스냅샷 이후의 모든 이벤트를 적용하여 잔액 계산
        List<AbstractAccountEvent> events = accountEventStoreUseCase.getEvents(accountId, fromDate);
        for (AbstractAccountEvent event : events) {
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
     * SNAPSHOT_THRESHOLD 이상의 이벤트가 누적된 경우 스냅샷을 생성합니다.
     *
     * @param accountId 계좌 ID
     */
    private void checkAndSaveSnapshot(String accountId) {
        AccountSnapshot lastSnapshot = snapshotRepository.findById(accountId).orElse(null);
        LocalDateTime fromDate = lastSnapshot != null ?
                lastSnapshot.getSnapshotDate() :
                LocalDateTime.of(1970, 1, 1, 0, 0);

        // 마지막 스냅샷 이후 이벤트 수 확인
        long eventCount = accountEventStoreUseCase.countEventsAfterDate(accountId, fromDate);

        // 임계값 초과시 스냅샷 생성
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
        AccountSnapshot snapshot = AccountSnapshot.of(
                accountId,
                currentBalance,
                LocalDateTime.now()
        );
        snapshotRepository.save(snapshot);
    }

}