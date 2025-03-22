package com.example.cqrs.application.account.query.service

import com.example.cqrs.application.account.command.service.usecase.AccountEventStoreUseCase
import com.example.cqrs.application.account.query.service.usecase.AccountUseCase
import com.example.cqrs.infrastructure.eventstore.entity.base.AccountEventBaseEntity
import com.example.cqrs.infrastructure.persistence.command.entity.AccountEntity
import com.example.cqrs.infrastructure.persistence.command.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountEventStoreUseCase: AccountEventStoreUseCase
) : AccountUseCase {

    /**
     * 특정 계좌의 정보를 조회합니다.
     *
     * @param accountId 조회할 계좌의 ID
     */
    override fun getAccount(
        accountId: String
    ): AccountEntity {
        return accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("계좌 정보를 찾을 수 없습니다.") }
    }

    /**
     * 특정 계좌의 전체 이벤트 이력을 조회합니다.
     * 계좌 생성부터 현재까지의 모든 거래 이벤트를 시간순으로 반환합니다.
     *
     * @param accountId 조회할 계좌의 ID
     */
    override fun getAccountHistory(
        accountId: String
    ): List<AccountEventBaseEntity> {
        return accountEventStoreUseCase.getAllEvents(accountId)
    }

    /**
     * 특정 사용자가 수행한 모든 거래 이벤트를 조회합니다.
     * 이벤트 메타데이터의 사용자 ID를 기준으로 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자 관련 이벤트 목록
     */
    override fun getUserTransactions(
        userId: String
    ): List<AccountEventBaseEntity> {
        return accountEventStoreUseCase.findByMetadataUserId(userId)
    }

    /**
     * 연관된 거래들을 조회합니다.
     * 동일한 correlationId를 가진 이벤트들을 그룹으로 조회합니다.
     * 예를 들어, 계좌 이체 시 출금과 입금 이벤트를 함께 조회할 수 있습니다.
     *
     * @param correlationId 연관 ID
     * @return 연관된 이벤트 목록
     */
    override fun getRelatedTransactions(
        correlationId: String
    ): List<AccountEventBaseEntity> {
        return accountEventStoreUseCase.findByMetadataCorrelationId(correlationId)
    }

    /**
     * 활성 상태인 모든 계좌의 ID 목록을 조회합니다.
     * 스케줄러나 관리 작업에서 사용됩니다.
     * 현재 시스템에 존재하는 모든 계좌를 대상으로 합니다.
     *
     * @return 활성 계좌 ID 목록
     */
    override fun getActiveAccountIds(): List<String> {
        return accountRepository.findAll().map { it.accountId }
    }

}