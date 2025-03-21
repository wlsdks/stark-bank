package com.example.cqrs.command.service

import com.example.cqrs.command.dto.CreateAccountRequest
import com.example.cqrs.command.dto.DepositRequest
import com.example.cqrs.command.dto.TransferRequest
import com.example.cqrs.command.dto.WithdrawRequest
import com.example.cqrs.command.entity.AccountEntity
import com.example.cqrs.command.entity.AccountSnapshotEntity
import com.example.cqrs.command.entity.event.AccountCreatedEventEntity
import com.example.cqrs.command.entity.event.MoneyDepositedEventEntity
import com.example.cqrs.command.entity.event.MoneyWithdrawnEventEntity
import com.example.cqrs.command.entity.event.metadata.EventMetadata
import com.example.cqrs.command.repository.AccountRepository
import com.example.cqrs.command.repository.AccountSnapshotRepository
import com.example.cqrs.command.usecase.AccountCommandUseCase
import com.example.cqrs.command.usecase.AccountEventStoreUseCase
import com.example.cqrs.common.exception.ConcurrencyException
import com.example.cqrs.domain.Account
import org.springframework.context.ApplicationEventPublisher
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * 계좌 관련 Command(쓰기) 작업을 처리하는 서비스 구현체입니다.
 * 이벤트 소싱 패턴을 사용하여 모든 상태 변경을 이벤트로 저장하고,
 * CQRS 패턴의 Command(쓰기) 부분을 담당합니다.
 */
@Transactional(readOnly = true)
@Service
class AccountCommandService(
    private val accountEventStoreUseCase: AccountEventStoreUseCase,
    private val snapshotRepository: AccountSnapshotRepository,
    private val accountRepository: AccountRepository,
    private val eventPublisher: ApplicationEventPublisher
) : AccountCommandUseCase {

    companion object {
        // 스냅샷 생성 기준 이벤트 수
        private const val SNAPSHOT_THRESHOLD = 100
    }

    /**
     * 새로운 계좌를 생성합니다.
     * 계좌 생성 이벤트를 저장하고 발행합니다.
     *
     * @param request 계좌 생성 요청 DTO (계좌ID, 사용자ID 포함)
     * @throws IllegalArgumentException 계좌가 이미 존재하는 경우
     */
    @Transactional
    override fun createAccount(request: CreateAccountRequest) {
        // 계좌 중복 검사
        if (accountRepository.existsById(request.accountId)) {
            throw IllegalArgumentException("이미 존재하는 계좌입니다.")
        }

        // 계좌 생성 및 저장
        val account = AccountEntity.of(request.accountId, 0.0)
        accountRepository.save(account)

        // 계좌 생성 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, request.userId)
        val event = AccountCreatedEventEntity.of(
            request.accountId,
            LocalDateTime.now(),
            0.0,
            eventMetadata
        )

        // 이벤트 저장 및 발행
        accountEventStoreUseCase.save(event)
        eventPublisher.publishEvent(event)

        // 스냅샷 저장이 필요한지 확인하고 필요한 경우 저장합니다.
        checkAndSaveSnapshot(request.accountId)
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
    override fun depositMoney(request: DepositRequest) {
        validateAmount(request.amount)
        val account = accountRepository.findById(request.accountId)
            .orElseThrow { IllegalArgumentException("계좌를 찾을 수 없습니다.") }

        // DB 잔액 업데이트
        account.changeBalance(account.balance + request.amount)
        accountRepository.save(account)

        // 입금 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, request.userId)
        val event = MoneyDepositedEventEntity.of(
            request.accountId,
            LocalDateTime.now(),
            request.amount,
            eventMetadata
        )

        try {
            // 이벤트 저장 및 발행
            accountEventStoreUseCase.save(event)
            eventPublisher.publishEvent(event)

            // 스냅샷 저장이 필요한지 확인하고 필요한 경우 저장합니다.
            checkAndSaveSnapshot(request.accountId)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.")
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
    override fun withdrawMoney(request: WithdrawRequest) {
        validateAmount(request.amount)
        val account = accountRepository.findById(request.accountId)
            .orElseThrow { IllegalArgumentException("계좌를 찾을 수 없습니다.") }
        account.checkAvailableWithdraw(request.amount)

        // DB 잔액 업데이트
        account.changeBalance(account.balance - request.amount)
        accountRepository.save(account)

        // 출금 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, request.userId)
        val event = MoneyWithdrawnEventEntity.of(
            request.accountId,
            LocalDateTime.now(),
            request.amount,
            eventMetadata
        )

        try {
            // 이벤트 저장 및 발행
            accountEventStoreUseCase.save(event)
            eventPublisher.publishEvent(event)

            // 스냅샷 저장이 필요한지 확인하고 필요한 경우 저장합니다.
            checkAndSaveSnapshot(request.accountId)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.")
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
    override fun transfer(request: TransferRequest) {
        validateAmount(request.amount)
        val fromAccount = loadAccount(request.fromAccountId)
        fromAccount.checkAvailableWithdraw(request.amount)

        // 하나의 거래를 위한 correlation ID 생성
        val correlationId = UUID.randomUUID().toString()

        // 출금 이벤트 생성
        val withdrawMetadata = EventMetadata.of(correlationId, null, request.userId)
        val withdrawEvent = MoneyWithdrawnEventEntity.of(
            request.fromAccountId,
            LocalDateTime.now(),
            request.amount,
            withdrawMetadata
        )

        // 입금 이벤트 생성 (출금 이벤트를 원인으로 지정)
        val depositMetadata = EventMetadata.of(correlationId, withdrawEvent.id?.toString(), request.userId)
        val depositEvent = MoneyDepositedEventEntity.of(
            request.toAccountId,
            LocalDateTime.now(),
            request.amount,
            depositMetadata
        )

        try {
            // 이벤트 저장 및 발행
            accountEventStoreUseCase.save(withdrawEvent)
            accountEventStoreUseCase.save(depositEvent)
            eventPublisher.publishEvent(withdrawEvent)
            eventPublisher.publishEvent(depositEvent)

            // 스냅샷 저장이 필요한지 확인하고 필요한 경우 저장합니다.
            checkAndSaveSnapshot(request.fromAccountId)
            checkAndSaveSnapshot(request.toAccountId)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.")
        }
    }

    /**
     * 계좌의 현재 상태를 로드합니다.
     *
     * @param accountId 계좌 ID
     * @return 계좌 도메인 객체
     */
    private fun loadAccount(accountId: String): Account {
        val balance = calculateCurrentBalance(accountId)
        return Account.of(accountId, balance)
    }

    /**
     * 계좌의 현재 잔액을 계산합니다.
     * 가장 최근 스냅샷부터 현재까지의 이벤트를 적용하여 계산합니다.
     *
     * @param accountId 계좌 ID
     * @return 현재 잔액
     */
    private fun calculateCurrentBalance(accountId: String): Double {
        // 최근 스냅샷 조회
        val snapshot = snapshotRepository.findById(accountId).orElse(null)

        val fromDate: LocalDateTime
        var balance: Double

        // 스냅샷이 있으면 스냅샷 시점부터, 없으면 처음부터 계산
        if (snapshot != null) {
            balance = snapshot.balance
            fromDate = snapshot.snapshotDate
        } else {
            balance = 0.0
            fromDate = LocalDateTime.of(1970, 1, 1, 0, 0)
        }

        // 스냅샷 이후의 모든 이벤트를 적용하여 잔액 계산
        val events = accountEventStoreUseCase.getEvents(accountId, fromDate)
        for (event in events) {
            when (event) {
                is MoneyDepositedEventEntity -> balance += event.amount ?: 0.0
                is MoneyWithdrawnEventEntity -> balance -= event.amount ?: 0.0
            }
        }

        return balance
    }

    /**
     * 금액의 유효성을 검증합니다.
     *
     * @param amount 검증할 금액
     * @throws IllegalArgumentException 금액이 0 이하인 경우
     */
    private fun validateAmount(amount: Double) {
        if (amount <= 0) {
            throw IllegalArgumentException("금액은 0보다 커야 합니다.")
        }
    }

    /**
     * 스냅샷 저장이 필요한지 확인하고 필요한 경우 저장합니다.
     * SNAPSHOT_THRESHOLD 이상의 이벤트가 누적된 경우 스냅샷을 생성합니다.
     *
     * @param accountId 계좌 ID
     */
    private fun checkAndSaveSnapshot(accountId: String) {
        val lastSnapshot = snapshotRepository.findById(accountId).orElse(null)

        val fromDate = lastSnapshot?.snapshotDate
            ?: LocalDateTime.of(1970, 1, 1, 0, 0)

        // 마지막 스냅샷 이후 이벤트 수 확인
        val eventCount = accountEventStoreUseCase.countEventsAfterDate(accountId, fromDate)

        // 임계값 초과시 스냅샷 생성
        if (eventCount >= SNAPSHOT_THRESHOLD) {
            saveSnapshot(accountId)
        }
    }

    /**
     * 현재 상태의 스냅샷을 저장합니다.
     *
     * @param accountId 계좌 ID
     */
    private fun saveSnapshot(accountId: String) {
        val currentBalance = calculateCurrentBalance(accountId)
        val snapshot = AccountSnapshotEntity.of(
            accountId,
            currentBalance,
            LocalDateTime.now()
        )
        snapshotRepository.save(snapshot)
    }

}