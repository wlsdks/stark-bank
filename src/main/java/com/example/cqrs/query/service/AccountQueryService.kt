package com.example.cqrs.query.service

import com.example.cqrs.query.document.AccountDocument
import com.example.cqrs.query.repository.AccountQueryMongoRepository
import com.example.cqrs.query.usecase.AccountQueryUseCase
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