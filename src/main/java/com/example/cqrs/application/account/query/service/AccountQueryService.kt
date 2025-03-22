package com.example.cqrs.application.account.query.service

import com.example.cqrs.application.account.query.service.usecase.AccountQueryUseCase
import com.example.cqrs.infrastructure.persistence.query.document.AccountDocument
import com.example.cqrs.infrastructure.persistence.query.repository.AccountMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 계좌 조회(Query) 서비스 구현체
 * MongoDB에서 계좌 정보를 조회하는 기능을 제공합니다.
 */
@Transactional(readOnly = true)
@Service
class AccountQueryService(
    private val accountMongoRepository: AccountMongoRepository
) : AccountQueryUseCase {

    private val log = LoggerFactory.getLogger(AccountQueryService::class.java)

    /**
     * 계좌 ID로 계좌 정보를 조회합니다.
     *
     * @param accountId 조회할 계좌 ID
     * @return 계좌 정보 (MongoDB Document) 또는 null (계좌가 없는 경우)
     */
    override fun getAccount(accountId: String): AccountDocument? {
        log.debug("계좌 정보 조회 (쿼리 모델): {}", accountId)
        return accountMongoRepository.findByAccountId(accountId)
    }

}