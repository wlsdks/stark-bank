package com.example.cqrs.application.account.query.service

import com.example.cqrs.application.account.event.retry.AccountQueryUseCase
import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument
import com.example.cqrs.infrastructure.persistence.query.repository.AccountQueryMongoRepository
import org.springframework.stereotype.Service

@Service
class AccountQueryService(
    private val accountQueryMongoRepository: AccountQueryMongoRepository
) : AccountQueryUseCase {

    /**
     * 특정 계좌의 정보를 조회합니다.
     *
     * @param accountId 조회할 계좌의 ID
     */
    override fun getAccount(
        accountId: String
    ): AccountDocument? {
        return accountQueryMongoRepository.findByAccountId(accountId)
    }

}