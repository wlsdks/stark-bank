package com.example.cqrs.application.command.account.service

import com.example.cqrs.application.command.account.dto.request.CreateAccountRequest
import com.example.cqrs.application.command.account.dto.request.DepositRequest
import com.example.cqrs.application.command.account.dto.request.TransferRequest
import com.example.cqrs.application.command.account.dto.request.WithdrawRequest
import com.example.cqrs.application.command.account.service.usecase.AccountCommandUseCase
import com.example.cqrs.common.exception.ConcurrencyException
import com.example.cqrs.common.exception.InsufficientBalanceException
import com.example.cqrs.infrastructure.eventstore.base.metadata.EventMetadata
import com.example.cqrs.infrastructure.eventstore.enumerate.OperationType
import com.example.cqrs.infrastructure.eventstore.event.account.AccountCreatedEvent
import com.example.cqrs.infrastructure.eventstore.event.money.*
import com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity
import com.example.cqrs.infrastructure.persistence.command.entity.AccountSnapshotEntity
import com.example.cqrs.infrastructure.persistence.command.repository.AccountEventRepository
import com.example.cqrs.infrastructure.persistence.command.repository.AccountRepository
import com.example.cqrs.infrastructure.persistence.command.repository.AccountSnapshotRepository
import com.example.cqrs.infrastructure.persistence.command.repository.EventStoreRepository
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
    private val accountEventRepository: AccountEventRepository,
    private val eventStoreRepository: EventStoreRepository,
    private val snapshotRepository: AccountSnapshotRepository,
    private val accountRepository: AccountRepository,
    private val eventPublisher: ApplicationEventPublisher
) : AccountCommandUseCase {

    companion object {
        // 스냅샷 생성 기준 이벤트 수
        private const val SNAPSHOT_THRESHOLD = 100
    }

    /**
     * 새로운 계좌를 생성
     * 계좌 생성 이벤트를 저장하고 발행
     *
     * @param request 계좌 생성 요청 DTO
     * @return 생성된 계좌 ID
     * @throws IllegalArgumentException 계좌가 이미 존재하는 경우
     */
    @Transactional
    override fun createAccount(request: CreateAccountRequest): String {
        // 계좌 중복 검사
        if (accountRepository.existsById(request.accountId)) {
            throw IllegalArgumentException("이미 존재하는 계좌입니다: ${request.accountId}")
        }

        // 계좌 엔티티 생성 및 저장
        val account = AccountEntity.of(
            request.accountId,
            request.userId
        )
        accountRepository.save(account)

        // 계좌 생성 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, request.userId)
        val event = AccountCreatedEvent.of(
            accountId = request.accountId,
            amount = 0.0,
            eventDate = LocalDateTime.now(),
            metadata = eventMetadata
        )

        // 이벤트 저장 및 발행
        eventStoreRepository.save(event)
        eventPublisher.publishEvent(event)

        // 스냅샷 저장이 필요한지 확인
        checkAndCreateSnapshot(request.accountId)

        return request.accountId
    }

    /**
     * 계좌에 입금
     * 잔액 업데이트 및 입금 이벤트 발행
     *
     * @param request 입금 요청 DTO
     * @return 입금 후 잔액
     * @throws IllegalArgumentException 금액이 유효하지 않은 경우
     */
    @Transactional
    override fun depositMoney(request: DepositRequest): Double {
        // 금액 유효성 검증
        validateAmount(request.amount)

        // 계좌 조회
        val account = getAccount(request.accountId)

        // 잔액 업데이트
        val newBalance = account.balance + request.amount
        account.changeBalance(newBalance)
        accountRepository.save(account)

        // 입금 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, request.userId)
        val event = MoneyDepositedEvent.of(
            accountId = request.accountId,
            amount = request.amount,
            eventDate = LocalDateTime.now(),
            metadata = eventMetadata
        )

        try {
            // 이벤트 저장 및 발행
            eventStoreRepository.save(event)
            eventPublisher.publishEvent(event)

            // 스냅샷 저장이 필요한지 확인
            checkAndCreateSnapshot(request.accountId)

            return newBalance
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.")
        }
    }

    /**
     * 계좌에서 출금
     * 잔액 확인, 업데이트 및 출금 이벤트 발행
     *
     * @param request 출금 요청 DTO
     * @return 출금 후 잔액
     * @throws IllegalArgumentException 금액이 유효하지 않은 경우
     * @throws InsufficientBalanceException 잔액이 부족한 경우
     */
    @Transactional
    override fun withdrawMoney(request: WithdrawRequest): Double {
        // 금액 유효성 검증
        validateAmount(request.amount)

        // 계좌 조회
        val account = getAccount(request.accountId)

        // 출금 가능 여부 확인
        if (account.balance < request.amount) {
            val failEvent = createBalanceChangeFailedEvent(
                request.accountId,
                request.amount,
                request.userId,
                "잔액 부족",
                OperationType.WITHDRAW
            )
            eventStoreRepository.save(failEvent)
            eventPublisher.publishEvent(failEvent)

            throw InsufficientBalanceException(
                "잔액이 부족합니다. 현재 잔액: ${account.balance}, 요청 금액: ${request.amount}"
            )
        }

        // 잔액 업데이트
        val newBalance = account.balance - request.amount
        account.changeBalance(newBalance)
        accountRepository.save(account)

        // 출금 이벤트 객체 생성
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, request.userId)
        val event = MoneyWithdrawnEvent.of(
            accountId = request.accountId,
            amount = request.amount,
            eventDate = LocalDateTime.now(),
            metadata = eventMetadata
        )

        try {
            // 이벤트 저장 및 발행
            eventStoreRepository.save(event)
            eventPublisher.publishEvent(event)

            // 스냅샷 저장이 필요한지 확인
            checkAndCreateSnapshot(request.accountId)

            return newBalance
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.")
        }
    }

    /**
     * 계좌 이체 수행
     * 출금 및 입금 이벤트를 하나의 트랜잭션으로 처리
     *
     * @param request 이체 요청 DTO
     * @throws IllegalArgumentException 금액이 유효하지 않은 경우
     * @throws InsufficientBalanceException 잔액이 부족한 경우
     */
    @Transactional
    override fun transfer(request: TransferRequest) {
        // 금액 유효성 검증
        validateAmount(request.amount)

        // 동일 계좌 이체 방지
        if (request.fromAccountId == request.toAccountId) {
            throw IllegalArgumentException("출금 계좌와 입금 계좌가 동일합니다.")
        }

        // 출금 계좌 조회
        val fromAccount = getAccount(request.fromAccountId)

        // 입금 계좌 조회
        val toAccount = getAccount(request.toAccountId)

        // 출금 가능 여부 확인
        if (fromAccount.balance < request.amount) {
            val failEvent = createBalanceChangeFailedEvent(
                request.fromAccountId,
                request.amount,
                request.userId,
                "잔액 부족",
                OperationType.TRANSFER
            )
            eventStoreRepository.save(failEvent)
            eventPublisher.publishEvent(failEvent)

            throw InsufficientBalanceException(
                "잔액이 부족합니다. 현재 잔액: ${fromAccount.balance}, 요청 금액: ${request.amount}"
            )
        }

        // 하나의 거래를 위한 correlation ID 생성
        val correlationId = UUID.randomUUID().toString()

        // 출금 처리
        fromAccount.changeBalance(fromAccount.balance - request.amount)
        accountRepository.save(fromAccount)

        // 이체 출금 이벤트 생성
        val withdrawMetadata = EventMetadata.of(correlationId, null, request.userId)
        val withdrawEvent = MoneyTransferredOutEvent.of(
            accountId = request.fromAccountId,
            targetAccountId = request.toAccountId,
            amount = request.amount,
            eventDate = LocalDateTime.now(),
            metadata = withdrawMetadata
        )

        try {
            // 출금 이벤트 저장 및 발행
            val savedWithdrawEvent =
                eventStoreRepository.save(withdrawEvent) as MoneyTransferredOutEvent
            eventPublisher.publishEvent(savedWithdrawEvent)

            // 입금 처리
            toAccount.changeBalance(toAccount.balance + request.amount)
            accountRepository.save(toAccount)

            // 이체 입금 이벤트 생성 (출금 이벤트를 원인으로 연결)
            val depositMetadata = EventMetadata.of(
                correlationId,
                savedWithdrawEvent.id?.toString(),
                request.userId
            )
            val depositEvent = MoneyTransferredInEvent.of(
                accountId = request.toAccountId,
                sourceAccountId = request.fromAccountId,
                amount = request.amount,
                eventDate = LocalDateTime.now(),
                metadata = depositMetadata
            )

            // 입금 이벤트 저장 및 발행
            eventStoreRepository.save(depositEvent)
            eventPublisher.publishEvent(depositEvent)

            // 스냅샷 저장이 필요한지 확인
            checkAndCreateSnapshot(request.fromAccountId)
            checkAndCreateSnapshot(request.toAccountId)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw ConcurrencyException("동시성 충돌이 발생했습니다. 다시 시도해주세요.")
        }
    }

    /**
     * 잔액 변경 실패 이벤트 생성
     */
    private fun createBalanceChangeFailedEvent(
        accountId: String,
        amount: Double,
        userId: String,
        reason: String,
        operationType: OperationType
    ): BalanceChangeFailedEvent {
        val correlationId = UUID.randomUUID().toString()
        val eventMetadata = EventMetadata.of(correlationId, null, userId)

        return BalanceChangeFailedEvent.of(
            accountId = accountId,
            amount = amount,
            reason = reason,
            operationType = operationType,
            eventDate = LocalDateTime.now(),
            metadata = eventMetadata
        )
    }

    /**
     * 계좌 정보 조회
     */
    private fun getAccount(accountId: String): AccountEntity {
        return accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("계좌를 찾을 수 없습니다: $accountId") }
    }

    /**
     * 금액 유효성 검증
     */
    private fun validateAmount(amount: Double) {
        if (amount <= 0) {
            throw IllegalArgumentException("금액은 0보다 커야 합니다. 입력 금액: $amount")
        }
    }

    /**
     * 스냅샷 생성 필요 여부 확인 및 생성
     */
    private fun checkAndCreateSnapshot(accountId: String) {
        val lastSnapshot = snapshotRepository.findTopByAccountIdOrderBySnapshotDateDesc(accountId)

        val fromDate = lastSnapshot?.snapshotDate
            ?: LocalDateTime.of(1970, 1, 1, 0, 0)

        // 마지막 스냅샷 이후 이벤트 수 확인
        val eventCount = accountEventRepository.countByAccountIdAndEventDateAfter(accountId, fromDate)

        // 임계값 초과시 스냅샷 생성
        if (eventCount >= SNAPSHOT_THRESHOLD) {
            createSnapshot(accountId)
        }
    }

    /**
     * 현재 상태의 스냅샷 생성
     */
    private fun createSnapshot(accountId: String) {
        val account = getAccount(accountId)

        // 최신 이벤트 조회
        val latestEvent = accountEventRepository.findByAccountIdOrderByEventDateDesc(accountId).firstOrNull()
            ?: return // 이벤트가 없으면 스냅샷 생성 불필요

        val snapshot = AccountSnapshotEntity.of(
            accountId = accountId,
            balance = account.balance,
            snapshotDate = LocalDateTime.now(),
            lastEventId = latestEvent.id ?: 0
        )

        snapshotRepository.save(snapshot)
    }

}